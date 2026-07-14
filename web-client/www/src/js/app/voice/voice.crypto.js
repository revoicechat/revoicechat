export class VoiceCrypto {
    #key;

    constructor(){
    }

    async init(){
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

        this.#key = await crypto.subtle.importKey('jwk', importKey, "AES-GCM", true, ["encrypt","decrypt"]);
    }

    async encrypt(data){
        const iv = crypto.getRandomValues(new Uint8Array(12));
        const encryptData = await crypto.subtle.encrypt({name:"AES-GCM", iv}, this.#key, data);
        const buffer = new ArrayBuffer(12 + encryptData.byteLength);
        const view = new Uint8Array(buffer);

        // Set IV
        view.set(iv, 0);
        
        // Set payload
        view.set(new Uint8Array(encryptData), 12);
       
        return buffer;
    }

    async decrypt(data){
        const iv = new Uint8Array(data, 0, 12);
        const payload = new Uint8Array(data, 12, data.byteLength - 12);
        return await crypto.subtle.decrypt({name:"AES-GCM", iv}, this.#key, payload);
    }
}
