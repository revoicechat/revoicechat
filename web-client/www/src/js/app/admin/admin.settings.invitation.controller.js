import CoreServer from "../core/core.server.js";
import Modal from "../../component/modal.component.js";
import { copyToClipboard } from "../../lib/tools.js";
import { i18n } from "../../lib/i18n.js";

export default class AdminSettingsInvitationController {

    load() {
        this.#invitationLoad().then(() => this.#invitationEventHandler())
    }

    #invitationEventHandler() {
        document.getElementById('admin-setting-invitation-create').addEventListener('click', () => this.#invitationCreate());
    }

    async #invitationLoad() {
        const result = await CoreServer.fetch(`/invitation/application`);
        if (result) {
            const list = document.getElementById("admin-setting-invitation");
            list.innerHTML = "";
            for (const invitation of result) {
                list.appendChild(this.#invitationCreateItem(invitation));
            }
        }
    }

    async #invitationCreate() {
        let invitationCategory = 'UNIQUE'
        Modal.toggle({
            title: i18n.translateOne("server.invitation.new"),
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("common.confirm"),
            showCancelButton: true,
            cancelButtonText: i18n.translateOne("common.cancel"),
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
            if (result.value) {
                await CoreServer.fetch(`/invitation/application?category=${invitationCategory}`, 'POST');
                await this.#invitationLoad();
            }
        });
    }

    #invitationCreateItem(data) {
        const DIV = document.createElement('div');
        DIV.id = data.id;
        DIV.className = "invitation config-item";

        // Name
        const DIV_NAME = document.createElement('div');
        DIV_NAME.className = "name invitation";
        DIV_NAME.innerText = `${data.id} (${data.status})`;
        DIV.appendChild(DIV_NAME);

        // Context menu
        const DIV_CM = document.createElement('div');
        DIV_CM.className = "context-menu";
        DIV_CM.appendChild(this.#createContextMenuButton("icon", "<revoice-icon-clipboard></revoice-icon-clipboard>", () => this.#invitationCopy(data.id)));
        DIV_CM.appendChild(this.#createContextMenuButton("icon", "<revoice-icon-trash></revoice-icon-trash>", () => this.#invitationDelete(data)));
        DIV.appendChild(DIV_CM);

        return DIV;
    }

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

    #invitationCopy(link) {
        const url = document.location.href.slice(0, -11) + `index.html?register=&invitation=${link}`;
        copyToClipboard(url);
    }

    #createContextMenuButton(className, innerHTML, onclick, title = "") {
        const DIV = document.createElement('div');
        DIV.className = className;
        DIV.innerHTML = innerHTML;
        DIV.onclick = onclick;
        DIV.title = title;
        return DIV;
    }
}
