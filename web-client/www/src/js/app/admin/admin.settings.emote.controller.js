import CoreServer from "../core/core.server.js";

export class AdminSettingsEmoteController {
    constructor() {
        this.#load();
    }

    async #load() {
        const response = await CoreServer.fetch(`/emote/global`);
        const emoji_manager = document.createElement('revoice-emoji-manager');
        emoji_manager.setAttribute('path', `global`);
        emoji_manager.id = "admin-setting-emotes-form";
        emoji_manager.innerHTML = `<script type="application/json" slot="emojis-data">${JSON.stringify(response)}</script>`;
        document.getElementById("admin-setting-content-emotes").appendChild(emoji_manager);
    }
}