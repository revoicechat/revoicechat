export default class Listener {
    #id;
    #decoder;
    #playhead;
    #muted;
    #gainNode;
    #outputGain;
    #audioContext;
    #controller;

    constructor(id, controller, codec, settings, audioContext, outputGain) {
        this.#id = id;
        this.#controller = controller;
        this.#audioContext = audioContext;

        // Set user state from settings
        this.#muted = settings.muted;
        this.#gainNode = audioContext.createGain();
        this.#gainNode.gain.setValueAtTime(settings.volume, audioContext.currentTime);

        // Set user decoder
        this.#decoder = new AudioDecoder({
            output: (audioData) => { this.#playback(audioData) },
            error: (error) => { throw new Error(`Decoder setup failed:\n${error.name}\nCurrent codec :${codec}`) },
        });

        this.#decoder.configure(codec);
        this.#playhead = 0;
        this.#outputGain = outputGain;
    }

    async close() {
        await this.#decoder.flush();
        this.#decoder.close();
    }

    #playback(audioData) {
        const buffer = this.#audioContext.createBuffer(
            audioData.numberOfChannels,
            audioData.numberOfFrames,
            audioData.sampleRate
        );

        const channelData = new Float32Array(audioData.numberOfFrames);
        audioData.copyTo(channelData, { planeIndex: 0 });
        buffer.copyToChannel(channelData, 0);

        // Play the AudioBuffer
        const source = this.#audioContext.createBufferSource();
        source.buffer = buffer;

        source.connect(this.#gainNode); // connect audio source to gain
        this.#gainNode.connect(this.#outputGain); // connect user gain to main gain
        this.#outputGain.connect(this.#audioContext.destination); // connect main gain to output

        this.#playhead = Math.max(this.#playhead, this.#audioContext.currentTime) + buffer.duration;
        source.start(this.#playhead);
        audioData.close();
    }

    setMute(muted) {
        this.#muted = muted;
    }

    setVolume(volume) {
        this.#gainNode.gain.setValueAtTime(volume, this.#audioContext.currentTime);
    }

    decodeAudio(decodedVoice, selfDeaf) {
        const timestamp = decodedVoice.timestamp;
        const gateState = decodedVoice.user.gateState;
        const data = decodedVoice.data;

        // If user sending packet is locally muted OR we are deaf, we stop
        if (this.#muted || selfDeaf) {
            this.#controller.setUserGlow(this.#id, false);
            return;
        }

        // User gate open/close
        this.#controller.setUserGlow(this.#id, gateState);

        // Decode and read audio
        if (this.#decoder !== null && this.#decoder.state === "configured") {
            this.#decoder.decode(new EncodedAudioChunk({
                type: "key",
                timestamp: Number.parseInt(timestamp * 1000),
                data: new Uint8Array(data),
            }));
        } else {
            console.error(`User '${this.#id}' has no decoder`);
        }
    }
}