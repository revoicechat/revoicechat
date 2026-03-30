import MediaServer from "../media/media.server.js";
import CoreServer from "../core/core.server.js";

export class ServerSettingsMemberController {

    /**
     * @param {ServerSettingsController} serverSettings
     */
    constructor(serverSettings) {
        this.serverSettings = serverSettings
    }

    load() {
        CoreServer.fetch(`/server/${this.serverSettings.server.id}/user`, 'GET')
                .then(result => {
                    if (result) {
                        const sortedByDisplayName = [.../** @type {UserRepresentation[]} */(result)].sort((a, b) => {
                            return a.displayName.localeCompare(b.displayName);
                        });

                        if (sortedByDisplayName !== null) {
                            const userList = document.getElementById("server-setting-members");
                            userList.innerHTML = "";
                            for (const user of sortedByDisplayName) {
                                userList.appendChild(this.#memberItem(user));
                            }
                        }
                    }
                });
        document.getElementById("server-moderation-panel").setAttribute("server-id", this.serverSettings.server.id);
    }

    /**
     * @param {UserRepresentation} data
     * @return {HTMLDivElement}
     */
    #memberItem(data) {
        const DIV = document.createElement('div');
        DIV.id = data.id;
        DIV.className = `${data.id} config-item`;

        const profilePicture = MediaServer.profiles(data.id);

        DIV.innerHTML = `
            <div class="relative">
                <img src="${profilePicture}" alt="PFP" class="icon ring-2" data-id="${data.id}" />
            </div>
            <div class="user">
                <div class="name" title="${data.displayName}" id="user-name">${data.displayName}<div>
            </div>
        `;

        return DIV;
    }
}