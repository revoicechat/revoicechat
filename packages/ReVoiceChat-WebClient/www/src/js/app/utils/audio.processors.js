class MonoCollector extends AudioWorkletProcessor {
    process(inputs) {
        const input = inputs[0][0]; // mono
        if (input) {
            this.port.postMessage(new Float32Array(input));
        }
        return true; // NOSONAR - DO NOT CHANGE OR REMOVE
    }
}

class StereoCollector extends AudioWorkletProcessor {
    process(inputs) {
        const input = inputs[0];

        // If there is no input data yet, continue
        if (input.length === 0 || input[0].length === 0) return true; // NOSONAR - DO NOT CHANGE OR REMOVE

        const left = input[0];   // Float32Array
        const right = input[1];  // Float32Array (may be undefined if mono)

        const frames = left.length;
        const channels = input.length;

        // Interleave channels: LRLRLR...
        const interleaved = new Float32Array(frames * channels);

        if (channels === 2 && right) {
            for (let i = 0; i < frames; i++) {
                interleaved[i * 2] = left[i];
                interleaved[i * 2 + 1] = right[i];
            }
        } else {
            // Mono fallback
            interleaved.set(left);
        }

        this.port.postMessage({
            samples: interleaved,
            channels: channels
        });

        return true; // NOSONAR - DO NOT CHANGE OR REMOVE
    }
}

class NoiseGate extends AudioWorkletProcessor {
    static get parameterDescriptors() {
        return [
            { name: 'attack', defaultValue: 0.01 },   // seconds
            { name: 'release', defaultValue: 0.4 },   // seconds
            { name: 'threshold', defaultValue: -50, minValue: -80, maxValue: 0 }, // dB
            { name: 'smoothing', defaultValue: 0.9}
        ];
    }

    constructor() {
        super();
        this.gain = 0;
        this.gateFloor = this.dBToLinear(-80);
        this.smoothRMS = 0;
    }

    dBToLinear(db) {
        return Math.pow(10, db / 20);
    }

    rmsLevel(samples) {
        let sum = 0;
        for (let sample of samples) {
            sum += sample ** 2;
        }
        return Math.sqrt(sum / samples.length);
    }

    process(inputs, outputs, parameters) {
        const input = inputs[0][0];
        const output = outputs[0][0];

        if (!inputs?.[0]?.[0]) {
            // MUST output silence or Firefox may break the graph
            output.fill(0);
            return;
        }

        const threshold = this.dBToLinear(parameters.threshold[0]);
        const attackCoeff = Math.exp(-1 / (parameters.attack[0] * globalThis.sampleRate / 1000));
        const releaseCoeff = Math.exp(-1 / (parameters.release[0] * globalThis.sampleRate / 1000));
        const rms = this.rmsLevel(input);

        const smoothing = parameters.smoothing[0];
        this.smoothRMS = this.smoothRMS ? this.smoothRMS * smoothing + rms * (1 - smoothing) : rms;

        // Gate logic
        if (this.smoothRMS > threshold) {
            // Gate open
            this.gain = 1 - (1 - this.gain) * attackCoeff;
        } else {
            // Gate close
            this.gain = this.gain * releaseCoeff;

            if (this.gain <= this.gateFloor) {
                this.gain = this.gateFloor;
            }
        }

        // Apply gain
        for (let i = 0; i < input.length; i++) {
            output[i] = input[i] * this.gain;
        }

        // Determine open/close state (with hysteresis to avoid flicker)
        const openNow = this.smoothRMS > threshold * 1.1; // 10% hysteresis
        if (openNow !== this.isOpen) {
            this.isOpen = openNow;
            this.port.postMessage({ open: this.isOpen });
        }
    }
}

registerProcessor("MonoCollector", MonoCollector);
registerProcessor("StereoCollector", StereoCollector);
registerProcessor('NoiseGate', NoiseGate);