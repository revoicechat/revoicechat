import {SpinnerOnButton} from "../../component/button.spinner.component.js";
import {i18n} from "../../lib/i18n.js";
import CoreServer from "../core/core.server.js";
import Modal from "../../component/modal.component.js";
import MediaServer from "../media/media.server.js";

export class ServerSettingsOverviewController {
    #newPictureFile = null;

    /**
     * @param {ServerSettingsController} serverSettings
     */
    constructor(serverSettings) {
        this.serverSettings = serverSettings
    }

    load() {
        document.getElementById('server-setting-overview-uuid').innerText = this.serverSettings.server.id;
        document.getElementById('server-setting-overview-name').innerText = this.serverSettings.server.name;
        document.getElementById('server-setting-overview-name-input').value = this.serverSettings.server.name;

        const settingServerPicture = document.getElementById("setting-server-picture");
        settingServerPicture.src = MediaServer.serverProfiles(this.serverSettings.server.id);
        settingServerPicture.dataset.id = this.serverSettings.server.id;
        const settingServerPictureNewPath = document.getElementById("overview-server-picture");
        const settingServerPictureNewFile = document.getElementById("overview-server-picture-new");
        settingServerPictureNewFile.onchange = () => {
            const file = settingServerPictureNewFile.files[0];
            if (file) {
                this.#newPictureFile = file;
                settingServerPictureNewPath.value = file.name;
                settingServerPicture.src = URL.createObjectURL(file);
                settingServerPicture.style.display = "block";
            }
        };
    }

    /**
     * @param {string[]} flattenRisks
     * @param {boolean} isAdmin
     */
    handleRisks(isAdmin, flattenRisks) {
        const overviewRisks = new Set(['SERVER_UPDATE']);
        if (isAdmin || flattenRisks.some(elem => overviewRisks.has(elem))) {
            this.#addOverviewEventHandler();
        } else {
            this.#removeOverviewEventHandler();
        }
    }

    #addOverviewEventHandler() {
        document.getElementById('server-setting-overview-name').classList.add('hidden');
        document.getElementById('server-setting-overview-name-input').classList.remove('hidden');
        const button = document.getElementById(`server-setting-overview-save`);
        button.classList.remove('hidden');
        button.onclick = () => this.#overviewSave();

        const buttonUpload = document.getElementById(`server-overview-select-picture`);
        buttonUpload.classList.remove('hidden');
        buttonUpload.onclick = () => document.getElementById("overview-server-picture-new").click();;

    }

    #removeOverviewEventHandler() {
        document.getElementById('server-setting-overview-name').classList.remove('hidden');
        document.getElementById('server-setting-overview-name-input').classList.add('hidden');
        const button = document.getElementById(`server-setting-overview-save`);
        button.classList.add('hidden');
        button.onclick = null;
    }

    async #overviewSave() {
        const spinner = new SpinnerOnButton("server-setting-overview-save")
        spinner.run()
        await this.#nameUpdate(spinner)
        await this.#pictureUpdate()
        spinner.success()
    }

    async #pictureUpdate() {
        const settingUserPictureNewPath = document.getElementById("overview-server-picture");
        if (settingUserPictureNewPath.value && this.#newPictureFile) {
            const formData = new FormData();
            formData.append("file", this.#newPictureFile);
            await MediaServer.fetch(`/profiles/${this.serverSettings.server.id}`, 'POST', formData);
            this.#newPictureFile = null
            settingUserPictureNewPath.value = null
        }
    }

    async #nameUpdate(spinner) {
        const serverName = document.getElementById("server-setting-overview-name-input").value;

        if (!serverName) {
            spinner.error();
            Modal.toggleError(i18n.translateOne("server.settings.name.error"));
            return;
        }

        /** @type {ServerRepresentation} */
        const result = await CoreServer.fetch(`/server/${this.serverSettings.server.id}`, 'PATCH', { name: serverName })
        if (result) {
            this.serverSettings.server.name = result.name;
            this.load();
        }
    }
}