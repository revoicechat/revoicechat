import { LargePacketSender } from "../utils/packet.js";
import { Multiplexer } from "./stream.multiplexing.js";
import Codec from "../utils/codec.js";

export default class Streamer {
    static CLOSE = 0;
    static CONNECTING = 1;
    static OPEN = 2;

    #state; // NOSONAR - for debugging purpose
    #socket;
    #user;
    #packetSender;
    #streamUrl;
    #token;
    #multiplexer = new Multiplexer();

    #displayMediaOptions = {
        video: true,
        audio: {
            channelCount: 2,
            sampleRate: 48000,
            echoCancellation: false,
            noiseSuppression: false,
            autoGainControl: false
        },
        preferCurrentTab: false,
        selfBrowserSurface: "include",
        systemAudio: "include",
        surfaceSwitching: "include",
        monitorTypeSurfaces: "include",
    }

    // Local playback
    #player;

    // Audio Encoder
    #audioCodec = structuredClone(Codec.STREAM_AUDIO);
    #audioBuffer = [];
    #audioBufferMaxLength = 1920; // 2ch x 48000Hz × 0.020 sec = 1920 samples
    #audioCollector;
    #audioContext;
    #audioEncoder;
    #audioTimestamp = 0;

    // Video Encoder
    #videoCodec;
    #videoMetadata;
    #videoEncoder;
    #videoEncoderInterval;
    #keyframeCounter = 0;
    #lastFrame = {
        height: 0,
        width: 0
    }

    constructor(streamUrl, user, token) {
        if (!streamUrl) {
            throw new Error('streamUrl is null or undefined');
        }

        if (!user) {
            throw new Error('user is null or undefined');
        }

        if (!token) {
            throw new Error('token is null or undefined');
        }

        this.#user = user;
        this.#streamUrl = streamUrl;
        this.#token = token;

        // Determine sampleRate
        const audioCtx = new AudioContext();
        this.#audioCodec.sampleRate = audioCtx.sampleRate;
        audioCtx.close();

        // Determine buffer length
        this.#audioBufferMaxLength = Number.parseInt(this.#audioCodec.sampleRate * this.#audioCodec.numberOfChannels * (this.#audioCodec.opus.frameDuration / 1_000_000));
    }

    async start(type, videoCodec) {
        if (!type) {
            throw new Error('type is null or undefined');
        }

        if (!videoCodec) {
            throw new Error('videoCodec is null or undefined');
        }

        this.#videoCodec = videoCodec;
        this.#state = Streamer.CONNECTING;

        // Test if codecs are supported first, so we don't open a socket for no reason
        const audioSupported = (await AudioEncoder.isConfigSupported(this.#audioCodec)).supported;
        const videoSupported = (await VideoEncoder.isConfigSupported(this.#videoCodec)).supported;
        if (!audioSupported || !videoSupported) {
            throw new Error("Audio or Video Encoder Codec not supported");
        }

        // Video player
        this.#player = document.createElement('video');
        this.#player.className = "content";
        this.#player.volume = 0; // IMPORTANT

        // Request capture
        try {
            switch (type) {
                case "webcam":
                    this.#player.srcObject = await navigator.mediaDevices.getUserMedia({ video: true, audio: false });
                    break;
                case "display":
                    this.#player.srcObject = await navigator.mediaDevices.getDisplayMedia(this.#displayMediaOptions);
                    break;
            }
        }
        catch (error) {
            this.stop();
            throw new Error(`MediaDevice setup failed:\n${error}`);
        }

        await this.#player.play();

        // Create WebSocket
        this.#socket = new WebSocket(`${this.#streamUrl}/${this.#user.id}/${type}`, ["Bearer." + this.#token]);
        this.#socket.binaryType = "arraybuffer";
        this.#socket.onclose = async () => { await this.stop(); };
        this.#socket.onerror = async (e) => { await this.stop(); console.error('Streamer : WebSocket error:', e) };

        // Create LargePacketSender
        this.#packetSender = new LargePacketSender(this.#socket);

        // Create Encoders
        this.#audioEncoder = new AudioEncoder({
            output: (frame) => {
                this.#packetSender.send(this.#multiplexer.processAudio(this.#audioTimestamp, frame));
            },
            error: (error) => { throw new Error(`Encoder setup failed:\n${error.name}\nCurrent codec :${this.#audioCodec.codec}`) },
        })

        this.#videoEncoder = new VideoEncoder({
            output: (frame, metadata) => {
                if (!this.#videoMetadata) {
                    this.#videoMetadata = {
                        "codec": null,
                        "codedHeight": null,
                        "codedWidth": null
                    }

                    this.#videoMetadata.codec = metadata.decoderConfig.codec;
                    this.#videoMetadata.codedHeight = metadata.decoderConfig.codedHeight;
                    this.#videoMetadata.codedWidth = metadata.decoderConfig.codedWidth;
                }

                const header = {
                    timestamp: Number.parseInt(performance.now()),
                    keyframe: frame.type === "key",
                    decoderConfig: this.#videoMetadata,
                }
                this.#packetSender.send(this.#multiplexer.processVideo(header, frame));
            },
            error: (error) => {
                this.stop();
                throw new Error(`Encoder setup failed:\n${error.name}\nCurrent codec :${this.#videoCodec.codec}`);
            },
        });

        // Configure Encoders
        this.#audioEncoder.configure(this.#audioCodec);
        this.#videoEncoder.configure(this.#videoCodec);

        // Process audio
        const audioTracks = this.#player.srcObject.getAudioTracks();
        if (audioTracks.length === 0) {
            console.warn("No audio track available");
        } else {
            // Init AudioContext
            this.#audioContext = new AudioContext({ sampleRate: this.#audioCodec.sampleRate });
            this.#audioContext.channelCountMode = "explicit";
            this.#audioContext.channelInterpretation = "discrete";
            this.#audioContext.channelCount = 2;

            await this.#audioContext.audioWorklet.addModule('src/js/app/utils/audio.processors.js');

            const audioStream = this.#audioContext.createMediaStreamSource(this.#player.srcObject);

            this.#audioCollector = new AudioWorkletNode(this.#audioContext, "StereoCollector", {
                channelCount: 2,
                channelCountMode: "explicit",
                channelInterpretation: "discrete"
            });

            audioStream.connect(this.#audioCollector);

            this.#audioCollector.port.onmessage = (event) => {
                const { samples, channels } = event.data;

                this.#audioBuffer.push(...samples);

                while (this.#audioBuffer.length >= this.#audioBufferMaxLength) {
                    const frames = this.#audioBuffer.slice(0, this.#audioBufferMaxLength);
                    const numberOfFrames = Number.parseInt(frames.length / channels);
                    this.#audioBuffer = this.#audioBuffer.slice(this.#audioBufferMaxLength);

                    const audioFrame = new AudioData({
                        format: "f32",
                        sampleRate: this.#audioContext.sampleRate,
                        numberOfFrames: numberOfFrames,
                        numberOfChannels: channels,
                        timestamp: this.#audioTimestamp,
                        data: new Float32Array(frames).buffer
                    });

                    if (this.#audioEncoder !== null && this.#audioEncoder.state === "configured") {
                        this.#audioEncoder.encode(audioFrame);
                    }
                    audioFrame.close();
                    this.#audioTimestamp += (numberOfFrames / this.#audioContext.sampleRate) * 1_000_000;
                }
            }
        }

        // Process video
        if (globalThis.MediaStreamTrackProcessor) {
            // Faster but not available everywhere
            const track = this.#player.srcObject.getVideoTracks()[0];
            const processor = new MediaStreamTrackProcessor({ track });
            const reader = processor.readable.getReader();

            // Grab frame
            this.#videoEncoderInterval = setInterval(async () => {
                const result = await reader.read();
                const frame = result.value;
                if (frame) {
                    await this.#reconfigureEncoderResolution(frame);
                    if (this.#videoEncoder) {
                        await this.#videoEncoder.encode(frame, { keyFrame: this.#isKeyframe() });
                    }
                    await frame.close();
                }
                else {
                    this.stop();
                }
            }, 1000 / this.#videoCodec.framerate)
        }
        else {
            // Fallback
            this.#videoEncoderInterval = setInterval(async () => {
                const frame = new VideoFrame(this.#player, { timestamp: performance.now() * 1000 });;
                await this.#reconfigureEncoderResolution(frame);
                if (this.#videoEncoder) {
                    await this.#videoEncoder.encode(frame, { keyFrame: this.#isKeyframe() });
                }
                frame.close();
            }, 1000 / this.#videoCodec.framerate)
        }

        this.#state = Streamer.OPEN;
        return this.#player;
    }

    #isKeyframe() {
        this.#keyframeCounter++;
        if (this.#keyframeCounter >= this.#videoCodec.framerate) {
            this.#keyframeCounter = 0;
            return true;
        }
        return false
    }

    async #reconfigureEncoderResolution(frame) {
        if (frame.codedHeight === this.#videoCodec.height && frame.codedWidth === this.#videoCodec.width) {
            // Captured frame and encoderCondig already match in width and height
            return;
        }

        if (this.#lastFrame.height === frame.codedHeight || this.#lastFrame.width === frame.codedWidth) {
            // Captured frame width and height didn't change since last frame
            return;
        }

        this.#lastFrame.width = frame.codedWidth;
        this.#lastFrame.height = frame.codedHeight;

        // Frame H & W are smaller than Max Codec H & W
        if (frame.codedHeight <= this.#videoCodec.height && frame.codedWidth <= this.#videoCodec.width) {
            await this.#setEncoderResolution(Number.parseInt(frame.codedHeight), Number.parseInt(frame.codedWidth));
            return;
        }

        const ratio = Math.min((this.#videoCodec.height / frame.codedHeight), (this.#videoCodec.width / frame.codedWidth));
        const height = Number.parseInt(frame.codedHeight * ratio);
        const width = Number.parseInt(frame.codedWidth * ratio);
        await this.#setEncoderResolution(height, width);
    }

    async #setEncoderResolution(height, width) {
        const newConfig = structuredClone(this.#videoCodec);
        newConfig.height = height;
        newConfig.width = width;

        if (this.#videoMetadata && this.#videoMetadata.decoderMetadata) {
            this.#videoMetadata.decoderMetadata.codedHeight = height;
            this.#videoMetadata.decoderMetadata.codedWidth = width;
        }

        if (this.#videoEncoder) {
            await this.#videoEncoder.configure(newConfig);
        }
    }

    async stop() {
        // Stop frame grabbing
        if (this.#videoEncoderInterval) {
            clearInterval(this.#videoEncoderInterval);
            this.#videoEncoderInterval = null;
        }

        // Close WebSocket
        if (this.#socket && (this.#socket.readyState === WebSocket.OPEN || this.#socket.readyState === WebSocket.CONNECTING)) {
            await this.#socket.close();
            this.#socket = null;
        }

        // Close encoder
        if (this.#videoEncoder && this.#videoEncoder.state !== "closed") {
            await this.#videoEncoder.close();
            this.#videoEncoder = null;
        }

        // Close audioContext
        if (this.#audioContext && this.#audioContext.state !== "closed") {
            this.#audioContext.close();
            this.#audioContext = null;
        }

        // Close playback
        if (this.#player) {
            await this.#player.pause();
            this.#player = null;
        }

        this.#state = Streamer.CLOSE;
    }
}