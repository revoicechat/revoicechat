import ServerSettingsController from "./server/server.settings.controller.js";
import { statusToColor } from "../lib/tools.js";
import MediaServer from "./media/media.server.js";
import CoreServer from "./core/core.server.js";
import { i18n } from "../lib/i18n.js";
import Modal from "../component/modal.component.js";
import Router from "./router.js";

export default class ServerController {
    /** @type {Room} */
    room;
    /** @type {string} */
    id;
    /** @type {string} */
    name;
    /** @type {ServerSettingsController} */
    settings;

    #popupData = null;

    /**
     * @param {Room} room
     */
    constructor(room, router, user) {
        this.room = room;
        this.router = router;
        this.user = user;
    }

    async load() {
        /** @type {ServerRepresentation[]} */
        const result = await CoreServer.fetch("/server", 'GET');

        if (result === null) {
            return;
        }

        // Create instances list
        const instancesList = document.getElementById('instances');
        instancesList.innerHTML = "";

        const instancesSortedByName = [...result].sort((a, b) => {
            return a.name.localeCompare(b.name);
        });

        for (const instance of instancesSortedByName) {
            const element = await this.#instanceElement(instance);
            if (element) {
                instancesList.appendChild(element);
            }
        }
        instancesList.appendChild(this.#joinInstanceElement());
        instancesList.appendChild(this.#discorverInstanceElement());

        if (this.user.isAdmin()) {
            instancesList.appendChild(this.#miscElement('revoice-icon-square-plus', "server.create.title", () => this.#create()));
            instancesList.appendChild(this.#miscElement('revoice-icon-wrench', "admin.title", () => this.router.routeTo(Router.ADMIN)));
        }

        // Select default server
        if (this.id) {
            this.select(this.id, this.name);
        } else {
            const server = instancesSortedByName[0]
            if (server) {
                this.select(server.id, server.name);
            }
        }

        this.settings = new ServerSettingsController(this);

        // Delete button
        if (this.user.isAdmin()) {
            const deleteButton = document.getElementById('server-delete');
            deleteButton.addEventListener('click', () => this.#delete());
            deleteButton.classList.remove('hidden');
        }
    }

    async #instanceElement(instance) {
        if (instance === undefined || instance === null) {
            return;
        }

        const DIV = document.createElement('div');
        DIV.classList.add('server-block');

        const BUTTON = document.createElement('button');
        BUTTON.id = instance.id;
        BUTTON.className = "element";
        BUTTON.title = instance.name;
        BUTTON.onclick = () => this.select(instance.id, instance.name);

        const notification = document.createElement('revoice-notification-dot')
        notification.id = `server-notification-dot-${instance.id}`
        notification.classList.add('server-notification')
        if (instance.unreadMessages.hasUnreadMessage) {
            notification.setAttribute('mentions', '' + instance.unreadMessages.mentions)
        } else {
            notification.classList.add('hidden')
        }
        DIV.appendChild(notification);

        const IMG = document.createElement('img');
        IMG.src = MediaServer.serverProfiles(instance.id);
        IMG.className = "icon";
        IMG.dataset.id = instance.id;
        BUTTON.appendChild(IMG);
        DIV.appendChild(BUTTON)
        return DIV;
    }

    #joinInstanceElement() {
        const BUTTON = document.createElement('button');

        BUTTON.className = "element";
        BUTTON.dataset.i18nTitle = "server.join.title";
        BUTTON.onclick = () => this.#join();

        const IMG = document.createElement('revoice-icon-circle-plus');
        IMG.className = "icon";
        BUTTON.appendChild(IMG);

        return BUTTON;
    }

    #discorverInstanceElement() {
        const BUTTON = document.createElement('button');

        BUTTON.className = "element";
        BUTTON.dataset.i18nTitle = "server.discover.title";
        BUTTON.onclick = () => this.#discover();

        const IMG = document.createElement('revoice-icon-telescope');
        IMG.className = "icon";
        BUTTON.appendChild(IMG);

        return BUTTON;
    }

    #miscElement(icon, title, callback) {
        const BUTTON = document.createElement('button');

        BUTTON.className = "element";
        BUTTON.dataset.i18nTitle = title;
        BUTTON.onclick = callback;

        const IMG = document.createElement(icon);
        IMG.className = "icon";
        BUTTON.appendChild(IMG);

        return BUTTON;
    }

    #join() {
        Modal.toggle({
            title: i18n.translateOne("server.join.title"),
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("server.join.confirm"),
            showCancelButton: true,
            cancelButtonText: i18n.translateOne("server.join.cancel"),
            width: "30rem",
            html: `
            <form class='popup'>
                <div>
                    <label for="invitation" data-i18n="login.host">${i18n.translateOne("server.join.invitation")}</label>
                    <br/>
                    <input type="text" name="host" id="invitation">
                </div>
            </form>`,
            didOpen: () => {
                document.getElementById('invitation').oninput = () => { this.#popupData = document.getElementById('invitation').value };
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                if (await CoreServer.fetch(`/server/join/${this.#popupData}`, 'POST')) {
                    await this.load();
                }
            }
        });
    }

    #discover() {
        Modal.toggle({
            title: i18n.translateOne("server.discover.title"),
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("server.join.confirm"),
            showCancelButton: true,
            cancelButtonText: i18n.translateOne("server.join.cancel"),
            width: "30rem",
            html: `
            <form class='popup'>
                <div>
                    <select id='modal-serverId'>
                        <option value='' selected disabled>${i18n.translateOne("server.join.select")}</option>
                    </select>
                </div>
            </form>`,
            didOpen: async () => {
                const select = document.getElementById('modal-serverId');
                select.oninput = () => { this.#popupData = select.value };

                const publicServers = await CoreServer.fetch('/server/discover');
                for (const instance of publicServers) {
                    const option = document.createElement('option');
                    option.value = instance.id;
                    option.innerHTML = instance.name;
                    select.appendChild(option);
                }
            }
        }).then(async (result) => {
            if (result.isConfirmed && this.#popupData) {
                if (await CoreServer.fetch(`/server/${this.#popupData}/join/`, 'POST')) {
                    await this.load();
                }
            }
        });
    }

    #create() {
        Modal.toggle({
            title: i18n.translateOne("server.create.title"),
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("server.create.confirm"),
            showCancelButton: true,
            cancelButtonText: i18n.translateOne("server.create.cancel"),
            width: "30rem",
            html: `
            <form class='popup'>
                <div>
                    <label for="modal-server-name" data-i18n="server.create.name">${i18n.translateOne("server.create.name")}</label>
                    <br/>
                    <input type="text" id="modal-server-name">
                    <br/>
                    <label for="modal-server-type" data-i18n="server.create.name">${i18n.translateOne("server.create.type")}</label>
                    <select id='modal-server-type'>
                        <option value="PRIVATE">Private</option>
                        <option value="PUBLIC">Public</option>
                    </select>
                </div>
            </form>`,
            didOpen: () => {
                this.#popupData = { name: null, serverType: "PRIVATE" };
                document.getElementById('modal-server-name').oninput = () => { this.#popupData.name = document.getElementById('modal-server-name').value };
                document.getElementById('modal-server-type').oninput = () => { this.#popupData.serverType = document.getElementById('modal-server-type').value };
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                if (!this.#popupData.name) {
                    Modal.toggleError(i18n.translateOne("server.create.error.name"));
                    return;
                }
                if (await CoreServer.fetch(`/server/`, 'PUT', this.#popupData)) {
                    await this.load();
                }
            }
        });
    }

    #delete() {
        let confirm = false;
        Modal.toggle({
            title: i18n.translateOne("server.delete.title"),
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("server.delete.confirm"),
            confirmButtonClass: "background-red",
            showCancelButton: true,
            cancelButtonText: i18n.translateOne("common.cancel"),
            width: "30rem",
            html: `
            <form class='popup'>
                <div>
                    <label for="modal-server-name" data-i18n="server.delete.title">${i18n.translateOne("server.delete.confirm.input", this.name)}</label>
                    <br/>
                    <input type="text" id="modal-server-name">
                </div>
            </form>`,
            didOpen: () => {
                document.getElementById("modal-server-name").oninput = () => {
                    confirm = (document.getElementById("modal-server-name").value === this.name);
                }
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                if (confirm) {
                    if (await CoreServer.fetch(`/server/${this.id}`, 'DELETE')) {
                        this.id = null;
                        await this.load();
                        this.router.routeTo('app');
                    }
                }
                else {
                    Modal.toggleError(i18n.translateOne("server.delete.abort", this.name))
                }
            }
        });
    }

    select(id, name) {
        if (!id) {
            console.error("Server id is null or undefined");
            return;
        }

        const currentInstance = document.getElementById(this.id);
        if (currentInstance) {
            currentInstance.classList.remove('active');
        }

        document.getElementById(id).classList.add('active');

        this.#updateServerName(id, name);
        this.room.load(id);
        this.router.routeTo(Router.APP);
    }

    #updateServerName(id, name) {
        this.id = id;
        this.name = name;
        document.getElementById("server-name").innerText = name;
        document.getElementById("server-picture").src = MediaServer.serverProfiles(id);
        document.title = `ReVoiceChat - ${name}`;
    }

    /** @param {ServerUpdateNotification} data */
    update(data) {
        switch (data.action) {
            case "ADD":
                break;
            case "REMOVE":
                break;
            case "MODIFY": {
                if (data.server.id === this.id) {
                    this.#updateServerName(this.id, data.server.name);
                    this.room.load(this.id);
                }
                return;
            }
            default:
                return;
        }
    }

    /** @param {NewUserInServer} data */
    updateUserInServer(data) {
        if (this.id === data.server) {
            this.room.loadUsers();
        }
    }
}