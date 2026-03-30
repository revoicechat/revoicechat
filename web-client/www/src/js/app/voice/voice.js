import Codec from "../utils/codec.js";
import Listener from "./voice.listener.js";
import { EncodedVoice, DecodedVoice } from "./voice.transport.js";

export default class VoiceCall {
    "use strict";

    static CLOSE = 0;
    static CONNECTING = 1;
    static OPEN = 2;
    static DEFAULT_SETTINGS = {
        compressor: {
             enabled: true
        },
        gate: {
            threshold: -60,
        },
        self: {
            muted: false,
            deaf: false,
            volume: 1,
        },
        users: {}
    }
    static COMPRESSOR_SETTINGS = {
        attack: 0,
        knee: 40,
        ratio: 12,
        release: 0.25,
        reduction: 0,
        threshold: -50,
    }
    static GATE_SETTINGS = {
        attack: 0.01,
        release: 0.4,
        threshold: -60
    }

    #codec = structuredClone(Codec.DEFAULT_VOICE_USER);
    #socket;
    #encoder;
    #audioCollector;
    #audioContext;
    #audioTimestamp = 0;
    #compressorNode;
    #buffer = [];
    #bufferMaxLength = 960; // 48000Hz × 0.020 sec = 960 samples
    #gainNode;
    #gateNode;
    #user;
    #users = {};
    #state = 0;
    #settings = {};
    #gateState = false;
    #outputGain;
    #controller;

    constructor(user) {
        if (!user) {
            throw new Error('user is null or undefined');
        }

        this.#user = user;

        if (user.settings) {
            this.#settings = user.settings.voice;
        }
        else {
            this.#settings = DEFAULT_SETTINGS;
        }

        // Determine sampleRate
        const audioCtx = new AudioContext();
        this.#codec.sampleRate = audioCtx.sampleRate;
        audioCtx.close();

        // Determine buffer length
        this.#bufferMaxLength = Number.parseInt(this.#codec.sampleRate * (this.#codec.opus.frameDuration / 1_000_000));
    }

    async open(voiceUrl, roomId, token, controller, anormalClosureHandler) {
        if (!voiceUrl) {
            throw new Error('VoiceUrl is null or undefined');
        }

        if (!roomId) {
            throw new Error('roomId is null or undefined');
        }

        if (!token) {
            throw new Error('token is null or undefined');
        }

        this.#state = VoiceCall.CONNECTING;
        this.#controller = controller;

        // Create WebSocket
        this.#socket = new WebSocket(`${voiceUrl}/${roomId}`, ["Bearer." + token]);
        this.#socket.binaryType = "arraybuffer";

        // Setup encoder and transmitter
        await this.#encodeAudio();

        // Setup receiver and decoder
        this.#socket.onmessage = async (message) => { await this.#decodeAudio(new DecodedVoice(message.data)) }

        // Setup main output gain
        this.#outputGain = this.#audioContext.createGain();
        this.#outputGain.gain.setValueAtTime(this.#user.settings.getVoiceVolume(), this.#audioContext.currentTime);

        // Socket states
        this.#socket.onclose = async (e) => {
            await this.close();
            if (e.code !== 1000) {
                console.error('VoiceCall : anormal closure:', e)
                anormalClosureHandler(e.reason);
            }
        };
        this.#socket.onerror = async (e) => { await this.close(); console.error('VoiceCall : WebSocket error:', e) };

        this.#state = VoiceCall.OPEN;
    }

    async close() {
        this.#state = VoiceCall.CLOSE;

        // Close WebSocket
        if (this.#socket && (this.#socket.readyState === WebSocket.OPEN || this.#socket.readyState === WebSocket.CONNECTING)) {
            await this.#socket.close();
            this.#socket = null;
        }

        // Close self encoder
        if (this.#encoder && this.#encoder.state !== "closed") {
            this.#encoder.close();
            this.#encoder = null;
        }

        // Close audioContext
        if (this.#audioContext && this.#audioContext.state !== "closed") {
            this.#audioContext.close();
            this.#audioContext = null;
        }

        // For all users
        for (const [userId, user] of Object.entries(this.#users)) {
            // Flush and close all decoders
            if (user?.decoder?.state === 'configured') {
                try {
                    await user.decoder.flush();
                    await user.decoder.close();

                }
                catch (error) {
                    console.error(error);
                }

                user.decoder = null;
            }
            // Remove glow
            this.#controller.setUserGlow(userId, false);
        }

        this.#controller.setSelfGlow(false);
    }

    getState() {
        return this.#state;
    }

    getSettings() {
        return this.#settings;
    }

    async removeUser(userId) {
        if (userId && this.#users[userId]) {
            await this.#users[userId].close();
        }
        this.#users[userId] = null;
    }

    async setUserMute(userId, enabled) {
        if (this.#users[userId]) {
            this.#users[userId].setMute(enabled);
        }
        else {
            console.warn(`Unable to set user mute, ${userId} don't exist.`);
        }
    }

    async updateUserMute(userId) {
        if (this.#users[userId] && this.#settings.users[userId]) {
            this.#users[userId].setMute(this.#settings.users[userId].muted);
        }
        else {
            console.warn(`Unable to update user mute, ${userId} don't exist.`);
        }
    }

    updateUserVolume(userId) {
        if (this.#users[userId] && this.#settings.users[userId]) {
            this.#users[userId].setVolume(this.#settings.users[userId].volume);
        }
        else {
            console.warn(`Unable to set user volume, ${userId} don't exist.`);
        }
    }

    async toggleSelfMute() {
        // Need to be async !
        this.#settings.self.muted = !this.#settings.self.muted;
    }

    async setSelfMute(muted) {
        // Need to be async !
        this.#settings.self.muted = muted;
    }

    async getSelfMute() {
        // Need to be async !
        return this.#settings.self.muted;
    }

    async toggleSelfDeaf() {
        // Need to be async !
        this.#settings.self.deaf = !this.#settings.self.deaf;
    }

    async setSelfDeaf(deaf) {
        // Need to be async !
        this.#settings.self.deaf = deaf;
    }

    async getSelfDeaf() {
        // Need to be async !
        return this.#settings.self.deaf;
    }

    setSelfVolume(volume) {
        this.#settings.self.volume = volume;

        if (this.#gainNode) {
            this.#gainNode.gain.setValueAtTime(volume, this.#audioContext.currentTime);
        }
    }

    getSelfVolume() {
        if (this.#gainNode) {
            return this.#gainNode.gain;
        }
    }

    setGate(gateSettings) {
        this.#settings.gate = gateSettings;
        this.#gateNode.parameters.get("threshold").setValueAtTime(this.#settings.gate.threshold, this.#audioContext.currentTime);
    }

    setOutputVolume(volume) {
        if (this.#outputGain) {
            this.#outputGain.gain.setValueAtTime(volume, this.#audioContext.currentTime);
        }
    }

    setCompressor(compressorSetting) {
        console.warn("Changing compressor settings as no effect");
    }

    async #encodeAudio() {
        const supported = await AudioEncoder.isConfigSupported(this.#codec);
        if (!supported.supported) {
            throw new Error("Encoder Codec not supported");
        }

        // Setup Encoder
        this.#encoder = new AudioEncoder({
            output: (chunk) => {
                if (this.#socket.readyState === WebSocket.OPEN) {
                    this.#socket.send(new EncodedVoice(Number.parseInt(this.#audioTimestamp / 1000), this.#user.id, this.#gateState, EncodedVoice.user, chunk, false).data);
                }
            },
            error: (error) => { throw new Error(`Encoder setup failed:\n${error.name}\nCurrent codec :${this.#codec.codec}`) },
        });

        this.#encoder.configure(this.#codec)

        // Init AudioContext
        this.#audioContext = new AudioContext({ sampleRate: this.#codec.sampleRate });
        await this.#audioContext.audioWorklet.addModule('src/js/app/utils/audio.processors.js');

        /**
         * Audio routing 
         * microphone -> filter (LP + HP) -> gain -> gate -> compressor (optional) -> collector -> buffer -> encoder -> send
         */

        // Init Mic capture
        const micSource = this.#audioContext.createMediaStreamSource(await navigator.mediaDevices.getUserMedia({
            audio: {
                echoCancellation: false,
                noiseSuppression: false,
                autoGainControl: false
            }
        }));

        // Create Filters around voice frequency
        const filterHigh = this.#audioContext.createBiquadFilter();
        filterHigh.type = 'highpass';
        filterHigh.frequency.value = 80;
        filterHigh.Q.value = 0.7;

        const filterLow = this.#audioContext.createBiquadFilter();
        filterLow.type = 'lowpass';
        filterLow.frequency.value = 5000;
        filterLow.Q.value = 0.7;

        // Connect microphone to filter
        micSource.connect(filterHigh);
        filterHigh.connect(filterLow);

        // Create Gain node
        this.#gainNode = this.#audioContext.createGain();
        this.#gainNode.gain.setValueAtTime(this.#settings.self.volume, this.#audioContext.currentTime);

        // Connect filter to gain
        filterLow.connect(this.#gainNode);

        // Create Gate
        this.#gateNode = new AudioWorkletNode(this.#audioContext, "NoiseGate", {
            parameterData: {
                attack: VoiceCall.GATE_SETTINGS.attack,
                release: VoiceCall.GATE_SETTINGS.release,
                threshold: this.#settings.gate.threshold
            }
        });

        this.#gateNode.port.onmessage = (event) => {
            const state = event.data.open
            this.#gateState = state;

            if (this.#settings.self.muted) {
                this.#controller.setUserGlow(this.#user.id, false);
                this.#controller.setSelfGlow(false);
            } else {
                this.#controller.setUserGlow(this.#user.id, state);
                this.#controller.setSelfGlow(state);
            }
        }

        // Connect gain to gate
        this.#gainNode.connect(this.#gateNode);

        // Create AudioCollector
        this.#audioCollector = new AudioWorkletNode(this.#audioContext, "MonoCollector", {
            channelCount: 1,
            channelCountMode: "explicit",
            channelInterpretation: "speakers"
        });

        // Create compressor if enabled
        if (this.#settings.compressor.enabled) {
            this.#compressorNode = this.#audioContext.createDynamicsCompressor();
            this.#compressorNode.attack.setValueAtTime(VoiceCall.COMPRESSOR_SETTINGS.attack, this.#audioContext.currentTime);
            this.#compressorNode.knee.setValueAtTime(VoiceCall.COMPRESSOR_SETTINGS.knee, this.#audioContext.currentTime);
            this.#compressorNode.ratio.setValueAtTime(VoiceCall.COMPRESSOR_SETTINGS.ratio, this.#audioContext.currentTime);
            this.#compressorNode.release.setValueAtTime(VoiceCall.COMPRESSOR_SETTINGS.release, this.#audioContext.currentTime);
            this.#compressorNode.threshold.setValueAtTime(VoiceCall.COMPRESSOR_SETTINGS.threshold, this.#audioContext.currentTime);

            // Connect gate to compressor
            this.#gateNode.connect(this.#compressorNode);

            // Connect compressor to audioCollector
            this.#compressorNode.connect(this.#audioCollector);
        } else {
            // Connect gate to audioCollector (i.e. bypass compressor)
            this.#gateNode.connect(this.#audioCollector);
        }

        this.#audioCollector.port.onmessage = (event) => {
            // We don't do anything if we are self muted
            if (this.#settings.self.muted || !this.#audioContext) {
                return;
            }

            const samples = event.data;

            if (!samples || samples.some(v => Number.isNaN(v))) {
                console.warn("Invalid samples", samples);
            }

            // Push samples to buffer
            this.#buffer.push(...samples);

            // While buffer is full
            while (this.#buffer.length >= this.#bufferMaxLength) {
                // Get 1 audio frames
                const frame = this.#buffer.slice(0, this.#bufferMaxLength);

                // Remove this frame from buffer
                this.#buffer = this.#buffer.slice(this.#bufferMaxLength);

                // Create audioData object to feed encoder
                const audioData = new AudioData({
                    format: "f32-planar",
                    sampleRate: this.#audioContext.sampleRate,
                    numberOfFrames: frame.length,
                    numberOfChannels: 1,
                    timestamp: this.#audioTimestamp,
                    data: new Float32Array(frame).buffer
                });

                // Feed encoder
                if (this.#encoder !== null && this.#encoder.state === "configured") {
                    this.#encoder.encode(audioData);
                }
                else {
                    console.error("Self has no encoder");
                }

                audioData.close();

                // Update audioTimestamp (add 20ms / 20000µs)
                this.#audioTimestamp += (frame.length / this.#audioContext.sampleRate) * 1_000_000;
            }
        }
    }

    async #decodeAudio(decodedVoice) {
        const userId = decodedVoice.user.id;
        const userType = decodedVoice.user.type;

        // User has no settings yet
        if (!this.#settings.users[userId]) {
            this.#settings.users[userId] = { muted: false, volume: 1 };
        }

        // User has no Listener yet
        if (!this.#users[userId]) {
            const listenerCodec = (userType === EncodedVoice.music ? Codec.DEFAULT_VOICE_MUSIC : Codec.DEFAULT_VOICE_USER);
            const isSupported = (await AudioDecoder.isConfigSupported(listenerCodec)).supported;
            if (isSupported) {
                this.#users[userId] = new Listener(userId, this.#controller, listenerCodec, this.#settings.users[userId], this.#audioContext, this.#outputGain);
            } else {
                throw new Error("Decoder Codec not supported");
            }
        }

        // Decode audio through Listener
        this.#users[userId].decodeAudio(decodedVoice, this.#settings.self.deaf);
    }
}