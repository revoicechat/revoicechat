import {copyToClipboard} from "../../lib/tools.js";
import CoreServer from "../core/core.server.js";
import Modal from "../../component/modal.component.js";
import {i18n} from "../../lib/i18n.js";

export class ServerSettingsInvitationController {
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
        const invitationRisks = new Set(['SERVER_INVITATION_ADD', 'SERVER_INVITATION_FETCH']);
        if (isAdmin || flattenRisks.some(elem => invitationRisks.has(elem))) {
            void this.#invitationLoad();
            document.getElementById('server-setting-invitation-create').onclick = () => this.#invitationCreate();
        } else {
            document.getElementById('server-setting-invitation-create').onclick = null;
            if (this.serverSettings.currentTab === "invitations") {
                this.serverSettings.select('overview');
            }
        }
    }

    async #invitationLoad() {
        const serverId = this.serverSettings.server.id;

        /** @type {InvitationRepresentation[]} */
        const result = await CoreServer.fetch(`/invitation/server/${serverId}`, 'GET');

        if (result) {
            const list = document.getElementById("server-setting-invitation");
            list.innerHTML = "";

            for (const invitation of result) {
                if (invitation.status === 'CREATED') {
                    list.appendChild(this.#invitationCreateItem(invitation));
                }
                if (invitation.status === 'PERMANENT') {
                    list.appendChild(this.#invitationCreateItem(invitation, "(PERMANENT)"));
                }
            }
        }
    }

    async #invitationCreate() {
        const serverId = this.serverSettings.server.id;

        let invitationCategory = 'UNIQUE'
        Modal.toggle({
            title: i18n.translateOne("server.invitation.new"),
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("server.join.confirm"),
            showCancelButton: true,
            cancelButtonText: i18n.translateOne("server.join.cancel"),
            width: "30rem",
            html: `
            <form class='popup'>
                <select id='modal-serverId'>
                    <option value='UNIQUE'    data-i18n="server.invitation.category.unique" selected>unique</option>
                    <option value='PERMANENT' data-i18n="server.invitation.category.permanent">permanent</option>
                </select>
            </form>`,
            didOpen: async () => {
                const select = document.getElementById('modal-serverId');
                select.oninput = () => { invitationCategory = select.value };
                i18n.translatePage(document.getElementById("modal-serverId"))
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                await CoreServer.fetch(`/invitation/server/${serverId}?category=${invitationCategory}`, 'POST');
                void this.#invitationLoad();
            }
        });
    }

    /**
     * @param {InvitationRepresentation} data
     * @param {string} additional
     * @return {HTMLDivElement}
     */
    #invitationCreateItem(data, additional = "") {
        const DIV = document.createElement('div');
        DIV.id = data.id;
        DIV.className = "config-item";

        // Name
        const DIV_NAME = document.createElement('div');
        DIV_NAME.className = "name invitation";
        DIV_NAME.innerText = `${data.id} ${additional}`;
        DIV.appendChild(DIV_NAME);

        // Context menu
        const DIV_CM = document.createElement('div');
        DIV_CM.className = "context-menu";
        DIV_CM.appendChild(this.#createContextMenuButton("icon", "<revoice-icon-clipboard></revoice-icon-clipboard>", () => this.#invitationCopy(data.id)));
        DIV_CM.appendChild(this.#createContextMenuButton("icon", "<revoice-icon-trash></revoice-icon-trash>", () => this.#invitationDelete(data)));
        DIV.appendChild(DIV_CM);

        return DIV;
    }

    /** @param {InvitationRepresentation} data */
    #invitationDelete(data) {
        Modal.toggle({
            title: `Delete invitation '${data.id}'`,
            showCancelButton: true,
            focusCancel: true,
            confirmButtonText: i18n.translateOne("modal.delete"),
            confirmButtonClass: "danger",
        }).then(async (result) => {
            if (result.isConfirmed) {
                await CoreServer.fetch(`/invitation/${data.id}`, 'DELETE');
                void this.#invitationLoad();
            }
        });
    }

    /** @param {string} link */
    #invitationCopy(link) {
        void copyToClipboard(link);
    }

    /**
     * @param {string} className
     * @param {string} innerHTML
     * @param {() => void} onclick
     * @param {string} title
     * @return {HTMLDivElement}
     */
    #createContextMenuButton(className, innerHTML, onclick, title = "") {
        const DIV = document.createElement('div');
        DIV.className = className;
        DIV.innerHTML = innerHTML;
        DIV.onclick = onclick;
        DIV.title = title;
        return DIV;
    }
}