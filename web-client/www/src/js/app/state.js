export default class State {
    /** @type {ReVoiceChat} */
    #rvc;

    /** @param {ReVoiceChat} rvc */
    constructor(rvc) {
        this.#rvc = rvc;
    }

    save() {
        const state = {
            server: {
                id: this.#rvc.server.id,
                name: this.#rvc.server.name,
            },
            room: {
                id: this.#rvc.room.id,
                name: this.#rvc.room.name,
                type: this.#rvc.room.type,
            },
            user: {
                id: this.#rvc.user.id,
                displayName: this.#rvc.user.displayName,
            },
            chat: {
                mode: "send",
                editId: null,
                attachmentMaxSize: 0,
            }
        }

        sessionStorage.setItem('lastState', JSON.stringify(state));
    }

    load() {
        const lastState = JSON.parse(sessionStorage.getItem('lastState'));
        if (lastState) {
            this.#rvc.server.id = lastState.server.id;
            this.#rvc.server.name = lastState.server.name;
            this.#rvc.room.id = lastState.room.id;
            this.#rvc.room.name = lastState.room.name;
            this.#rvc.room.type = lastState.room.type;
            this.#rvc.user.id = lastState.user.id;
            this.#rvc.user.displayName = lastState.user.displayName;
        }
    }
}