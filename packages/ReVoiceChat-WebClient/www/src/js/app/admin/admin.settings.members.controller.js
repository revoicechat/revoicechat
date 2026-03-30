import CoreServer from "../core/core.server.js";
import MediaServer from "../media/media.server.js";
import Modal from "../../component/modal.component.js";

export default class AdminSettingsMembersController {
    #user;

    constructor(user) {
        this.#user = user;
    }

    load() {
        CoreServer.fetch("/user").then((users) => {

            this.#buildUserList("admin-setting-admins-list", users.filter(u => u.type === "ADMIN"))
            this.#buildUserList("admin-setting-members-list", users)
        });
    }

    #buildUserList(eltId, users) {
        const usersNode = document.getElementById(eltId);
        usersNode.innerHTML = "";
        for (const user of users) {
            const DIV = document.createElement('div');
            DIV.id = user.id;
            if (user.type === "BOT") {
                const icon = document.createElement('span')
                icon.className = "user-icon"
                icon.innerHTML = `<revoice-icon-robot></revoice-icon-robot>`
                DIV.appendChild(icon);
            }
            DIV.appendChild(this.#buildProfilPictureElement(user))
            DIV.appendChild(this.#buildUserInfos(user))
            if (user.id === this.#user.id) {
                DIV.className = "user config-item self";
            } else {
                DIV.addEventListener('click', () => this.#update(user))
                DIV.className = "user config-item";
            }
            usersNode.appendChild(DIV)
        }
    }

    #buildProfilPictureElement(user) {
        const profilPicture = document.createElement('img');
        profilPicture.id = `user-picture-${user.id}`
        profilPicture.src = MediaServer.profiles(user.id);
        profilPicture.alt = "PFP"
        profilPicture.className = "icon ring-2"
        return profilPicture;
    }

    #buildUserInfos(user) {
        const userInfos = document.createElement('div');
        userInfos.className = "card-list"
        const DIV = document.createElement('div');
        DIV.appendChild(this.#userName(user))
        DIV.appendChild(this.#loginTooltip(user))
        userInfos.appendChild(DIV)
        userInfos.appendChild(this.#idTooltip(user))
        return userInfos;
    }

    #loginTooltip(user) {
        const userLogin = document.createElement('span');
        userLogin.innerText = `#${user.login}`
        userLogin.className = "user-login-tooltip"
        return userLogin;
    }

    #idTooltip(user) {
        const userId = document.createElement('span');
        userId.className = "id-tooltip"
        userId.innerText = user.id
        return userId;
    }

    #userName(user) {
        const userName = document.createElement('span');
        userName.innerText = user.displayName
        return userName;
    }

    #update(user) {
        let type = user.type;
        Modal.toggle({
            title: `Update user`,
            html: `
            <form class='popup'>
                <div class="server-structure-form-group">
                    <label for="userType">Type</label>
                    <select id='userType'>
                        <option value='USER'>User</option>
                        <option value='BOT'>Bot</option>
                        <option value='ADMIN'>Admin</option>
                    </select>
                </div>
            </form > `,
            didOpen: () => {
                document.getElementById('userType').value = user.type
                document.getElementById('userType').oninput = () => { type = document.getElementById('userType').value };
            },
            showCancelButton: true,
            confirmButtonText: "Update",
            allowOutsideClick: false,
        }).then(async (result) => {
            if (result.value) {
                await CoreServer.fetch(`/user/${user.id}`, 'PATCH', {
                    displayName: user.displayName,
                    type: type
                });
                this.load();
            }
        });
    }
}