import CoreServer from "../core/core.server.js";

export class ServerSettingsEmoteController {

    /**
     * @param {ServerSettingsController} serverSettings
     */
    constructor(serverSettings) {
        this.serverSettings = serverSettings
    }

    /**
     * @param {string[]} flattenRisks
     * @param {boolean} isAdmin
     */
    handleRisks(isAdmin, flattenRisks) {
        const emoteRisks = new Set(['ADD_EMOTE', 'UPDATE_EMOTE', 'REMOVE_EMOTE']);
        if (isAdmin || flattenRisks.some(elem => emoteRisks.has(elem))) {
            void this.#emotesLoad();
        } else if (this.serverSettings.currentTab === "emotes") {
            this.serverSettings.select('overview');
        }
    }

    async #emotesLoad() {
        const oldManager = document.getElementById("server-setting-emotes-form");
        if (oldManager) {
            oldManager.remove();
        }
        
        /** @type {EmoteRepresentation[]} */
        const response = await CoreServer.fetch(`/emote/server/${this.serverSettings.server.id}`);
        const emoji_manager = document.createElement('revoice-emoji-manager');
        emoji_manager.setAttribute('path', `server/${this.serverSettings.server.id}`);
        emoji_manager.id = "server-setting-emotes-form";
        emoji_manager.innerHTML = `<script type="application/json" slot="emojis-data">${JSON.stringify(response)}</script>`;
        document.getElementById("server-setting-content-emotes").appendChild(emoji_manager);
    }
}