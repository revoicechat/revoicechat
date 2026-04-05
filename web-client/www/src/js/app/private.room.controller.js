import TextController from "./text.controller.js";
import Modal from "../component/modal.component.js";
import { i18n } from "../lib/i18n.js";
import CoreServer from "./core/core.server.js";
import RoomController from "./room.controller.js";
import { getTextSanction } from "./utils/sanctions.utils.js";
import MediaServer from "./media/media.server.js";

export default class PrivateRoomController extends RoomController {
    id;
    textController

    /** @param {UserController} user */
    constructor(user) {
        super(user);
        this.textController = new TextController(user, this, true);
        this.attachEvents();
    }

    async load() {
        const rooms = await CoreServer.fetch(`/private-message`);
        if (rooms) {
            console.log(rooms);

            const list = document.getElementById('private-messages-list');
            for (const room of rooms) {
                list.appendChild(this.#createRoomSelector(room))
            }
        }

        await this.textController.getAttachmentMaxSize();

        const sanction = getTextSanction(null, this.user.sanctions)
        if (sanction) {
            this.textController.disableText(sanction)
        }
    }

    attachEvents() {
        document.getElementById('private-message-new').addEventListener('click', () => this.#newRoom());
        this.textController.attachEvents();
    }

    #createRoomSelector(room) {
        if (room === undefined || room === null) {
            return;
        }

        const id = room.id;
        const name = room.name || room.users[0].displayName;
        const profilePicture = MediaServer.profiles(room.users[0].id);

        const DIV = document.createElement('div');
        DIV.id = id;
        DIV.className = `${id} user-profile`
        DIV.innerHTML = `
            <div class="relative">
                <img src="${profilePicture}" alt="PFP" class="icon ring-2" data-id="${id}" name="user-picture-${id}" />
            </div>
            <div class="user">
                <h2 class="name" name="user-name-${id}" title="${name}" >${name}</h2>
            </div>
        `;

        DIV.addEventListener('click', () => { this.#selectRoom(id) })

        return DIV;
    }

    #newRoom() {
        let user = null;

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

                    const userElement = document.getElementById('modal-user');
                    userElement.oninput = () => { user = userElement.value };
                }
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                const data = {
                    "users": [],
                    "name": null
                }
                data.users.push(user);

                await CoreServer.fetch(`/private-message`, 'POST', data);
            }
        });
    }

    #selectRoom(id) {
        const lastRoom = document.getElementById(this.id);
        if (this.id && lastRoom) {
            lastRoom.classList.remove("active");
        }

        this.id = id;

        document.getElementById(id).classList.add("active");
        document.getElementById('private-message-controls').classList.remove('hidden');

        /*const sanction = getTextSanction(this.#serverId, this.user.sanctions)
        if (sanction) {
            this.textController.disableText(sanction)
        } else {
            this.textController.enableText(this.name)
        }*/

        this.textController.load(this.id);
    }
}