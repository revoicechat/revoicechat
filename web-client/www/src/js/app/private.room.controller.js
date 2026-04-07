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
            const list = document.getElementById('private-messages-list');
            list.innerHTML = "";
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
        document.getElementById('private-open').addEventListener('dblclick', () => this.load());
        this.textController.attachEvents();
    }

    #createRoomSelector(room) {
        if (room === undefined || room === null) {
            return;
        }

        const id = room.id;
        let name = "";
        let profilePicture = null;

        const usersNoSelf = [...room.users].filter((user) => {
            return user.id != this.user.id;
        })

        if (usersNoSelf.length === 0) {
            name = this.user.displayName;
            profilePicture = MediaServer.profiles(this.user.id);
        } else if (usersNoSelf.length === 1) {
            name = room.name || usersNoSelf[0].displayName
            profilePicture = MediaServer.profiles(usersNoSelf[0].id);
        } else {
            let names = "";
            let i = 0;
            for (const user of usersNoSelf) {
                if (i === 0) {
                    names += user.displayName;
                }
                else {
                    names += `, ${user.displayName}`;
                }
                i++
            }

            name = room.name || names;
        }

        const DIV = document.createElement('div');
        DIV.id = id;
        DIV.className = `${id} user-profile`

        const pictureRoot = document.createElement('div');
        pictureRoot.className = "relative";
        const picture = document.createElement('img');
        picture.src = profilePicture;
        picture.className = "icon";
        picture.setAttribute('data-id', id);
        picture.name = `user-picture-${id}`;

        pictureRoot.appendChild(picture);
        DIV.appendChild(pictureRoot);

        const nameRoot = document.createElement('div');
        nameRoot.className = "user";
        const nameElement = document.createElement('h2');
        nameElement.className = "name";
        nameElement.name = `user-name-${id}`;
        nameElement.title = name;
        nameElement.innerText = name;

        nameRoot.appendChild(nameElement);
        DIV.appendChild(nameRoot);

        const notification = document.createElement('revoice-notification-dot')
        notification.id = `room-extension-dot-${room.id}`
        notification.style.margin = 'auto'
        if (room.unreadMessages.hasUnreadMessage) {
            notification.setAttribute('mentions', '' + room.unreadMessages.mentions)
        } else {
            notification.className = 'hidden'
        }
        DIV.appendChild(notification);

        DIV.addEventListener('click', () => { this.#selectRoom(id, name, profilePicture) })

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

                await this.load();
            }
        });
    }

    #selectRoom(id, name, profilePicture) {
        const lastRoom = document.getElementById(this.id);
        if (this.id && lastRoom) {
            lastRoom.classList.remove("active");
        }

        this.id = id;

        document.getElementById(id).classList.add("active");
        document.getElementById('private-message-controls').classList.remove('hidden');
        document.getElementById('private-room-name').innerText = name;
        document.getElementById('private-room-icon').src = profilePicture;
        document.getElementById('private-room-icon').classList.remove('hidden');

        /*const sanction = getTextSanction(this.#serverId, this.user.sanctions)
        if (sanction) {
            this.textController.disableText(sanction)
        } else {
            this.textController.enableText(this.name)
        }*/

        this.textController.load(this.id);
    }
}