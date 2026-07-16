/**
 * Voice Crypto
 * Ask voice key :
 * [ 1 byte  ] Packet type = 0
 * [ N bytes ] Temporary public key
 *
 * Transmit voice key :
 * [ 1 byte ] Packet type = 1
 * [ N byte ] Encrypted Voice Key
 * 
 * Audio :
 * [  1 byte  ] Packet type = 2
 * [ 12 bytes ] Initiation Vector
 * [  N bytes ] Payload
 */

export class VoiceCrypto {
    #voiceKey;
    #socket;
    #tempKeyPair;
    #dispatchAudioCallback;

    constructor(socket) {
        this.#socket = socket;
    }

    async init(onlySelf) {
        if (onlySelf) {
            await this.#generateKey();
        } else {
            await this.#requestKey();
        }
    }

    async #generateKey() {
        this.#voiceKey = await crypto.subtle.generateKey(
            {
                name: "AES-GCM",
                length: 256,
            },
            true, // extractable
            ["encrypt", "decrypt"]
        );
    }

    async #requestKey() {
        this.#tempKeyPair = await crypto.subtle.generateKey(
            {
                name: "RSA-OAEP",
                modulusLength: 2048,
                publicExponent: new Uint8Array([1, 0, 1]),
                hash: "SHA-256",
            },
            true,
            ["encrypt", "decrypt"]
        );

        const exportedPublic = await crypto.subtle.exportKey(
            "spki",
            this.#tempKeyPair.publicKey
        );

        const buffer = new ArrayBuffer(1 + exportedPublic.byteLength);
        const uint8buffer = new Uint8Array(buffer);
        let offset = 0;

        // Set type 0
        uint8buffer.set([0], 0);
        offset += 1;

        // Set publicKey
        uint8buffer.set(new Uint8Array(exportedPublic), offset);

        this.#socket.send(buffer);
    }

    async #importVoiceKey(voiceKeyBuffer) {
        const decryptedVoiceKey = await crypto.subtle.decrypt(
            {
                name: "RSA-OAEP",
            },
            this.#tempKeyPair.privateKey,
            voiceKeyBuffer
        );

        this.#voiceKey = await crypto.subtle.importKey(
            'raw',
            decryptedVoiceKey,
            { name: "AES-GCM" }, 
            true, 
            ["encrypt", "decrypt"]
        );

        this.#tempKeyPair = null;
    }

    async #exportVoiceKey(publicKeyBuffer) {
        const publicKey = await crypto.subtle.importKey(
            "spki",
            publicKeyBuffer,
            {
                name: "RSA-OAEP",
                hash: "SHA-256",
            },
            false,
            ["encrypt"]
        );

        const exportedVoiceKey = await crypto.subtle.exportKey(
            "raw",
            this.#voiceKey
        );

        const encryptedVoiceKey = await crypto.subtle.encrypt(
            {
                name: "RSA-OAEP",
            },
            publicKey,
            exportedVoiceKey
        );

        const buffer = new ArrayBuffer(1 + encryptedVoiceKey.byteLength);
        const uint8buffer = new Uint8Array(buffer);
        let offset = 0;

        // Set type 1
        uint8buffer.set([1], offset);
        offset += 1;

        // Set data
        uint8buffer.set(new Uint8Array(encryptedVoiceKey), offset);

        console.log("Sending voiceKey")
        this.#socket.send(buffer);
    }

    async encrypt(data) {
        if (!this.#voiceKey) {
            console.warn("No voiceKey set");
            return;
        }

        const iv = crypto.getRandomValues(new Uint8Array(12));
        const encryptData = await crypto.subtle.encrypt({ name: "AES-GCM", iv }, this.#voiceKey, data);
        const buffer = new ArrayBuffer(1 + 12 + encryptData.byteLength);
        const uint8buffer = new Uint8Array(buffer);
        let offset = 0;

        // Set type 2
        uint8buffer.set([2], offset);
        offset += 1;

        // Set IV
        uint8buffer.set(iv, offset);
        offset += 12;

        // Set payload
        uint8buffer.set(new Uint8Array(encryptData), offset);

        return buffer;
    }

    async #decrypt(data) {
        if (!this.#voiceKey) {
            console.warn("No voiceKey set");
            return;
        }

        const iv = new Uint8Array(data, 0, 12);
        const payload = new Uint8Array(data, 12, data.byteLength - 12);
        return await crypto.subtle.decrypt({ name: "AES-GCM", iv }, this.#voiceKey, payload);
    }

    async process(data) {
        const type = new Uint8Array(data)[0];
        const payload = data.slice(1);

        switch (type) {
            case 0:
                await this.#exportVoiceKey(payload);
                return null;
            case 1:
                await this.#importVoiceKey(payload);
                return null;
            case 2:
                return await this.#decrypt(payload);
        }
    }
}
