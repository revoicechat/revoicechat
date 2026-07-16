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

    constructor(socket, dispatchAudio) {
        this.#socket = socket;
        this.#dispatchAudioCallback = dispatchAudio;

        console.log(this.#socket);
    }

    async init(onlySelf) {
        if (onlySelf) {
            await this.#generateKey();
        } else {
            await this.#requestKey();
        }
    }

    async #generateKey() {
        console.log("Generating voiceKey");
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
        console.log("Requesting voiceKey");
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
        console.log("Importing voiceKey");

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
        console.log("Exporting voiceKey");

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

        console.log("Encrypting voiceKey")

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
        console.log("Encrypting voice");

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
        console.log("Decrypting voice");

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
        const payload = new Uint8Array(data, 1, data.byteLength - 1);

        switch (type) {
            case 0:
                await this.#exportVoiceKey(payload);
                break;
            case 1:
                await this.#importVoiceKey(payload);
                break;
            case 2:
                await this.#dispatchAudioCallback(await this.#decrypt(payload));
                break;
        }
    }

    async test() {
        // TEST WITH FIXED KEY
        let importKey =
        {
            "alg": "A256GCM",
            "ext": true,
            "k": "JLBln1POmu05MCLuX0r-BnymysbNdS7x06US8wAq3fg",
            "key_ops": [
                "encrypt",
                "decrypt"
            ],
            "kty": "oct"
        }

        this.#voiceKey = await crypto.subtle.importKey('jwk', importKey, "AES-GCM", true, ["encrypt", "decrypt"]);
    }
}
