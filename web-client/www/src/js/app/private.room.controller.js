import TextController from "./text.controller.js";
import Modal from "../component/modal.component.js";
import { i18n } from "../lib/i18n.js";
import CoreServer from "./core/core.server.js";
import RoomController from "./room.controller.js";
import {getTextSanction} from "./utils/sanctions.utils.js";

export default class PrivateRoomController extends RoomController{
    /** @param {UserController} user */
    constructor(user) {
        super(user);
        this.textController = new TextController(user, this, true);
        this.attachEvents();
    }

    async load() {
        const rooms = await CoreServer.fetch(`/private-message`);
        if(rooms){
            
        }
        const sanction = getTextSanction(null, this.user.sanctions)
        if (sanction) {
            this.textController.disableText(sanction)
        }
    }

    attachEvents() {
        document.getElementById('private-message-new').addEventListener('click', () => this.#newPrivateRoom());
    }

    #newPrivateRoom() {
        Modal.toggle({
            title: i18n.translateOne("private.message.new"),
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("common.new"),
            showCancelButton: true,
            cancelButtonText: i18n.translateOne("common.cancel"),
            width: "30rem",
            html: `
            <form class='popup'>
                <select id="modal-user"></select>
            </form>`,
            didOpen: async () => {
                const select = document.getElementById('modal-user');
                const users = await CoreServer.fetch(`/user`);

                if (users) {
                    const sortedByDisplayName = [...users].sort((a, b) => {
                        return a.displayName.localeCompare(b.displayName);
                    });

                    for (const user of sortedByDisplayName) {
                        const option = document.createElement('option');
                        option.value = user.id;
                        option.innerText = user.displayName;
                        select.appendChild(option)
                    }
                }
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                await CoreServer.fetch(`/user/${document.getElementById('modal-user').value}/private-message`, 'POST');
            }
        });
    }
}