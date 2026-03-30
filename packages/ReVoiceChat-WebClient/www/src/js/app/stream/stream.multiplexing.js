export class Multiplexer {
    static VIDEO = 0;
    static AUDIO = 1;
    static CODEC_VIDEO = ["vp8", "vp09.00.10.08", "av01.0.04M.08"];

    /** Audio packet format :
     * [ 1 byte  ] Type
     * [ 4 bytes ] Timestamp (uint32)
     * [ 4 bytes ] Audio chunk length
     * [ X bytes ] Audio chunk 
    */
    processAudio(timestamp, chunk) {
        const headerSize = 1 + 4 + 4;
        const buffer = new ArrayBuffer(headerSize + chunk.byteLength);
        const view = new DataView(buffer);
        let offset = 0;

        // Stream type
        view.setUint8(offset, Multiplexer.AUDIO);
        offset += 1;

        // Timestamp
        view.setUint32(offset, Number.parseInt(timestamp / 1000), true);
        offset += 4;

        // Audio chunk length
        view.setUint32(offset, chunk.byteLength, true);
        offset += 4;

        // Audio chunk
        const payload = new Uint8Array(buffer, offset, chunk.byteLength);
        chunk.copyTo(payload);

        return buffer;
    }

    /**
     * @param {*} header 
     * @param {*} decoderConfig 
     * @param {*} frame 
     * 
     * Video packet format:
     * [ 1 byte  ] Type
     * [ 4 bytes ] Timestamp (uint32)
     * [ 1 byte  ] Keyframe (bool)
     * [ 1 byte  ] Codec (0: VP8, 1: VP9, 2: AV1)
     * [ 2 bytes ] Coded Height (max 65535)
     * [ 2 bytes ] Coded Width (max 65535)
     * [ 4 bytes ] Payload length
     * [ X bytes ] Payload
     */

    processVideo(header, frame) {
        const headerSize = 1 + 4 + 1 + 1 + 2 + 2 + 4;
        const buffer = new ArrayBuffer(headerSize + frame.byteLength);
        const view = new DataView(buffer);
        let offset = 0;

        // Stream type
        view.setUint8(offset, Multiplexer.VIDEO);
        offset += 1;

        // Timestamp
        view.setUint32(offset, header.timestamp, true);
        offset += 4;

        // Keyframe
        view.setUint8(offset++, header.keyframe);

        // decoderConfig : Codec
        view.setUint8(offset++, Multiplexer.CODEC_VIDEO.indexOf(header.decoderConfig.codec));

        // decoderConfig : Height
        view.setUint16(offset, header.decoderConfig.codedHeight, true);
        offset += 2;

        // decoderConfig : Width
        view.setUint16(offset, header.decoderConfig.codedWidth, true);
        offset += 2;

        // Payload length
        view.setUint32(offset, frame.byteLength, true);
        offset += 4;

        // Payload
        const payload = new Uint8Array(buffer, offset, frame.byteLength);
        frame.copyTo(payload);

        return buffer;
    }
}

export class Demultiplexer {
    #audioCallback;
    #videoCallback;

    constructor(audioCallback, videoCallback) {
        this.#audioCallback = audioCallback;
        this.#videoCallback = videoCallback;
    }

    process(rawData) {
        const buffer = rawData;
        const view = new DataView(buffer);
        let offset = 0;

        const streamType = view.getUint8(offset);

        if (streamType === Multiplexer.AUDIO) {
            this.#processAudio(buffer, view);
        } else {
            this.#processVideo(buffer, view);
        }
    }

    #processAudio(buffer, view) {
        let offset = 1;

        // Timestamp
        const timestamp = view.getUint32(offset, true);
        offset += 4;

        // Payload length
        const payloadLength = view.getUint32(offset, true);
        offset += 4;

        // Payload
        const payload = new Uint8Array(buffer, offset, payloadLength);

        this.#audioCallback(timestamp, payload);
    }

    #processVideo(buffer, view) {
        let offset = 1;

        const header = {
            timestamp: null,
            keyframe: null,
            decoderConfig: {
                codec: null,
                codedHeight: null,
                codedWidth: null,
                optimizeforlatency: true
            },
        }

        // Timestamp
        header.timestamp = view.getUint32(offset, true);
        offset += 4;

        // Keyframe
        header.keyframe = view.getUint8(offset++);

        // decoderConfig : Codec
        header.decoderConfig.codec = Multiplexer.CODEC_VIDEO[view.getUint8(offset++)];

        // decoderConfig : Height
        header.decoderConfig.codedHeight = view.getUint16(offset, true);
        offset += 2;

        // decoderConfig : Width
        header.decoderConfig.codedWidth = view.getUint16(offset, true);
        offset += 2;

        // Payload length
        const payloadLength = view.getUint32(offset, true);
        offset += 4;

        // Payload
        const payload = new Uint8Array(buffer, offset, payloadLength);

        this.#videoCallback(header, payload);
    }
}