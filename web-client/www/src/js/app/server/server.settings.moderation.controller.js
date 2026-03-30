export class ServerSettingsModerationController {

    /** @param {ServerSettingsController} serverSettings */
    constructor(serverSettings) {
        this.serverId = serverSettings.server.id
    }

    load() {
        document.getElementById("server-moderation-panel").setAttribute("server-id", this.serverId);
    }
}