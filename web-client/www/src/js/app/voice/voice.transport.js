/**
 * Voice Transport
 * Structure :
 * [  4 bytes ] Timestamp (uint32)
 * [  1 byte  ] User info ([1]Type; [2]Gate; [3]Mute)
 * [ 36 bytes ] User ID
 * [  4 bytes ] Payload length (uint32)
 * [  X bytes ] Payload (voice)
 */
export class EncodedVoice {
    static user = 0;
    static music = 1;
    
    data;

    constructor(timestamp, userId, userGateState, userType, audioData, userSelfMute){
        const headerSize = 4 + 1 + 36 + 4;
        const buffer = new ArrayBuffer(headerSize + audioData.byteLength);
        const view = new DataView(buffer);
        let offset = 0;

        // Timestamp
        view.setUint32(offset, Number.parseInt(timestamp / 1000), true);
        offset += 4;

        // User info
        let userInfo = 0;
        userInfo += userType ? 1 : 0;
        userInfo += userGateState ? 2 : 0;
        userInfo += userSelfMute ? 4 : 0
        view.setUint8(offset++, userInfo);

        // User ID
        new Uint8Array(buffer, offset, 36).set(new TextEncoder().encode(userId))
        offset += 36;

        // Payload length
        view.setUint32(offset, audioData.byteLength, true);
        offset += 4;

        // Payload
        const payload = new Uint8Array(buffer, offset, audioData.byteLength);
        audioData.copyTo(payload);        

        this.data = buffer;
    }
}

export class DecodedVoice {
    timestamp = null;
    user = {
        id: null,
        gateState: null,
        type: null,
        selfMute: false,
    }
    data = null;

    constructor(encodedPacket){
        let offset = 0;
        const buffer = encodedPacket;
        const view = new DataView(encodedPacket);

        // Timestamp
        this.timestamp = view.getUint32(offset, true);
        offset += 4;

        // User info
        const userInfo = view.getUint8(offset++);
        this.user.type = userInfo & 1;
        this.user.gateState = userInfo & 2;
        this.user.selfMute = userInfo & 4;

        // User ID
        this.user.id = new TextDecoder().decode(
            new Uint8Array(buffer, offset, 36)
        );
        offset += 36;

        // Payload
        const payloadLength = view.getUint32(offset, true);
        offset += 4;
        this.data = new Uint8Array(buffer, offset, payloadLength);
    }
}