import { LargePacketReceiver } from "../utils/packet.js";
import { Demultiplexer } from "./stream.multiplexing.js";
import Codec from "../utils/codec.js";

export default class Viewer {
    #socket;
    #demultiplexer;
    #streamUrl;
    #token;

    // Local playback
    #context;
    #canvas;
    #lastFrame = {
        clientWidth: 0,
        clientHeight: 0,
        codedWidth: 0,
        codedHeight: 0
    }

    // Audio decoder
    #audioCodec = structuredClone(Codec.STREAM_AUDIO);
    #audioContext;
    #audioDecoder;
    #audioGain;
    #audioVolume = 0.5;
    #audioPlayhead = 0;

    // Video decoder
    #videoDecoder;
    #videoDecoderKeyFrame = false;

    constructor(streamUrl, token, userSettings) {
        if (!streamUrl) {
            throw new Error('streamUrl is null or undefined');
        }

        if (!token) {
            throw new Error('token is null or undefined');
        }

        this.#streamUrl = streamUrl;
        this.#token = token;
        this.#audioVolume = userSettings.getStreamVolume();
    }

    async join(userId, streamName) {
        if (userId && streamName) {
            const audioSupported = await AudioDecoder.isConfigSupported(this.#audioCodec);

            if (!audioSupported) {
                console.error("Audio codec not supported");
                return null;
            }

            // Create WebSocket
            this.#socket = new WebSocket(`${this.#streamUrl}/${userId}/${streamName}`, ["Bearer." + this.#token]);
            this.#socket.binaryType = "arraybuffer";
            this.#socket.onclose = async () => { await this.leave(); };
            this.#socket.onerror = async (e) => { await this.leave(); console.error('Streamer : WebSocket error:', e) };

            this.#demultiplexer = new Demultiplexer(
                (timestamp, data) => { this.#decodeAudio(timestamp, data) },
                (header, data) => { this.#decodeVideo(header, data) }
            );

            const receiver = new LargePacketReceiver();
            receiver.init(this.#socket, (rawData) => { this.#demultiplexer.process(rawData) });

            // Video player
            this.#canvas = document.createElement("canvas");
            this.#context = this.#canvas.getContext("2d");

            // AudioContext
            this.#audioContext = new AudioContext({ sampleRate: this.#audioCodec.sampleRate });

            // Audio gain (volume)
            this.#audioGain = this.#audioContext.createGain();
            this.#audioGain.gain.setValueAtTime(this.#audioVolume, this.#audioContext.currentTime);
            this.#audioGain.channelCountMode = "explicit";
            this.#audioGain.channelInterpretation = "discrete";


            // Audio decoder
            this.#audioDecoder = new AudioDecoder({
                output: (chunk) => { this.#playbackAudio(chunk) },
                error: (error) => { throw new Error(`AudioDecoder setup failed:\n${error.name}\nCurrent codec :${this.#audioCodec.codec}`) },
            });
            await this.#audioDecoder.configure(this.#audioCodec);

            // Video decoder
            this.#videoDecoder = new VideoDecoder({
                output: (frame) => {
                    this.#reconfigureCanvasResolution(frame, this.#canvas);
                    this.#context.drawImage(frame, 0, 0, this.#canvas.width, this.#canvas.height);
                    frame.close();
                },
                error: (error) => { this.leave(); throw new Error(`VideoDecoder error:\n${error.name}`); }
            });

            return this.#canvas;
        }
    }

    #reconfigureCanvasResolution(frame, videoItem) {
        if (this.#lastFrame.clientWidth != videoItem.clientWidth || this.#lastFrame.clientHeight != videoItem.clientHeight ||
            this.#lastFrame.codedWidth != frame.codedWidth || this.#lastFrame.codedHeight != frame.codedHeight) {
            const ratio = Math.min((videoItem.clientHeight / frame.codedHeight), (videoItem.clientWidth / frame.codedWidth));
            this.#canvas.height = frame.codedHeight * ratio;
            this.#canvas.width = frame.codedWidth * ratio;

            this.#lastFrame.clientHeight = videoItem.clientHeight;
            this.#lastFrame.clientWidth = videoItem.clientWidth;
            this.#lastFrame.codedHeight = frame.codedHeight;
            this.#lastFrame.codedWidth = frame.codedWidth;
        }
    }

    async leave() {
        // WebSocket
        if (this.#socket && (this.#socket.readyState === WebSocket.OPEN || this.#socket.readyState === WebSocket.CONNECTING)) {
            await this.#socket.close();
            this.#socket = null;
        }

        // audioDecoder
        if (this.#audioDecoder && this.#audioDecoder.state !== "closed") {
            await this.#audioDecoder.close();
            this.#audioDecoder = null;
        }

        // audioContext
        if (this.#audioContext && this.#audioContext.state !== "closed") {
            this.#audioContext.close();
            this.#audioContext = null;
        }

        // videoDecoder
        if (this.#videoDecoder && this.#videoDecoder.state !== "closed") {
            await this.#videoDecoder.close();
            this.#videoDecoder = null;
        }

        // video playback
        if (this.#canvas && this.#context) {
            this.#canvas = null;
            this.#context = null;
        }
    }

    #decodeVideo(header, data) {
        // Decoder didn't get a keyFrame yet
        if (!this.#videoDecoderKeyFrame) {
            if (header.keyframe) {
                this.#videoDecoderKeyFrame = true;
            }
            return;
        }

        if (this.#videoDecoder.state === "unconfigured") {
            this.#videoDecoder.configure(header.decoderConfig);
        }

        this.#videoDecoder.decode(new EncodedVideoChunk({
            type: "key",
            timestamp: header.timestamp,
            data: new Uint8Array(data)
        }));
    }

    #decodeAudio(timestamp, data) {
        if (this.#audioDecoder !== null && this.#audioDecoder.state === "configured") {
            this.#audioDecoder.decode(new EncodedAudioChunk({
                type: "key",
                timestamp: timestamp * 1000,
                data: new Uint8Array(data),
            }));
        } else {
            console.error(`No AudioDecoder correctly configured found for this stream`);
        }
    }

    #playbackAudio(audioData) {
        const buffer = this.#audioContext.createBuffer(
            audioData.numberOfChannels,
            audioData.numberOfFrames,
            audioData.sampleRate
        );

        const interleaved = new Float32Array(audioData.numberOfFrames * audioData.numberOfChannels);
        audioData.copyTo(interleaved, { planeIndex: 0 });

        // De-interleave into buffer channels
        for (let ch = 0; ch < audioData.numberOfChannels; ch++) {
            const channelData = new Float32Array(audioData.numberOfFrames);
            for (let i = 0; i < audioData.numberOfFrames; i++) {
                channelData[i] = interleaved[i * audioData.numberOfChannels + ch];
            }
            buffer.copyToChannel(channelData, ch);
        }

        const source = this.#audioContext.createBufferSource();
        source.channelCount = buffer.numberOfChannels;
        source.channelCountMode = "explicit";
        source.channelInterpretation = "discrete";
        source.buffer = buffer;

        // Routing : decodedAudio -> gain (volume) -> output 
        source.connect(this.#audioGain);
        this.#audioGain.connect(this.#audioContext.destination);

        this.#audioPlayhead = Math.max(this.#audioPlayhead, this.#audioContext.currentTime) + buffer.duration;
        source.start(this.#audioPlayhead);
        audioData.close();
    }

    setVolume(value) {
        this.#audioVolume = value;
        this.#audioGain.gain.setValueAtTime(this.#audioVolume, this.#audioContext.currentTime);
    }

    getVolume() {
        return this.#audioVolume;
    }
}