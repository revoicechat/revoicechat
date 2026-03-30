export class ServerSettingsRoleController {

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
        const rolesRisks = new Set(['ADD_ROLE', 'UPDATE_ROLE', 'ADD_USER_ROLE']);
        if (isAdmin || flattenRisks.some(elem => rolesRisks.has(elem))) {
            this.#rolesLoad();
        } else {
            this.#rolesLoad(true);
            if (this.serverSettings.currentTab === "roles") {
                this.serverSettings.select('overview');
            }
        }
    }

    #rolesLoad() {
        document.getElementById("server-setting-roles-component").setAttribute("server-id", this.serverSettings.server.id)
    }
}