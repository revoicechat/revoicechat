import CoreServer from "../core/core.server.js";

export default class AdminSettingsOverviewController {
    constructor() {

    }

    async load() {
        const settings = await CoreServer.fetch("/settings")

        if (settings) {
            const node = document.getElementById("admin-setting-technical-list");
            node.innerHTML = `<table class="technical-list">
                          <thead>
                            <tr>
                              <th>key</th>
                              <th>value</th>
                            </tr>
                          </thead>
                          <tbody>
                            ${this.#extractToList(settings).map(item => `
                                <tr>
                                  <td>${item.key}</td>
                                  <td>${item.value}</td>
                                </tr>
                              `)
                    .join('')}
                          </tbody>
                        </table>`;
        }
    }

    #extractToList(settings) {
        return Object.entries(settings)
            .map(([key, value]) => ({ key: key, value: value }))
            .sort((a, b) => a.key.localeCompare(b.key));
    }
}