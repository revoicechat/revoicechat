import CoreServer from "../core/core.server.js";

export default class AdminSettingsServersController {
    constructor() {

    }

    async load() {
        const servers = await CoreServer.fetch("/server")
        if (servers) {
            const serversNode = document.getElementById("admin-setting-server-list");
            serversNode.innerHTML = "";

            const serversSortedByName = [...servers].sort((a, b) => {
                return a.name.localeCompare(b.name);
            });

            for (const server of serversSortedByName) {
                const DIV = document.createElement('div');
                DIV.id = server.id;
                DIV.className = "server config-item";
                DIV.appendChild(this.#buildInfos(server))
                serversNode.appendChild(DIV)
            }
        };
    }

    #buildInfos(server) {
        const serverInfos = document.createElement('div');
        serverInfos.className = "card-list"
        serverInfos.appendChild(this.#serverName(server))
        serverInfos.appendChild(this.#idTooltip(server))
        return serverInfos;
    }

    #idTooltip(server) {
        const serverId = document.createElement('span');
        serverId.className = "id-tooltip"
        serverId.innerText = server.id
        return serverId;
    }

    #serverName(server) {
        const serverName = document.createElement('span');
        serverName.innerText = server.name
        return serverName;
    }
}