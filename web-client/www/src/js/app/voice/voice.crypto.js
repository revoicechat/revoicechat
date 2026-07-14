export class VoiceCrypto {
    #key;
    #iv;

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
        this.#iv = new Uint8Array([127,144,120,208,195,35,175,245,173,95,45,220]);
    }

    async encrypt(data){
        const iv = this.#iv;
        return await crypto.subtle.encrypt({name:"AES-GCM", iv}, this.#key, data);
    }

    async decrypt(data){
        const iv = this.#iv;
        return await crypto.subtle.decrypt({name:"AES-GCM", iv}, this.#key, data);
    }
}
