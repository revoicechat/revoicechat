import UserSettingsController from "./user.settings.controller.js";
import {eraseCookie, statusToColor} from "../lib/tools.js";
import MediaServer from "./media/media.server.js";
import CoreServer from "./core/core.server.js";
import PrivateRoomController from "./private.room.controller.js";

export default class UserController {
    /** @type {UserSettingsController} */
    settings;
    /** @type {string} */
    id;
    /** @type {string} */
    displayName;
    /** @type {string} */
    #type;

    constructor() {
        this.privateRooms = new PrivateRoomController(this)
        this.settings = new UserSettingsController(this);
    }

    async load() {
        /** @type {UserRepresentation} */
        const result = await CoreServer.fetch(`/user/me`, 'GET');

        if (result !== null) {
            this.id = result.id;
            this.displayName = result.displayName;
            this.#type = result.type;
            
            document.getElementById("status-container").classList.add(result.id);
            document.getElementById("user-name").innerText = result.displayName;
            document.getElementById("user-status").innerText = result.status;
            document.getElementById("user-dot").setAttribute('color', statusToColor(result.status));

            const userPicture = document.getElementById("user-picture");
            userPicture.src = MediaServer.profiles(result.id);
            userPicture.dataset.id = result.id;
        }

        await this.settings.load();
        await this.privateRooms.load();
    }

    /** @param {UserRepresentation} data */
    update(data) {
        const id = data.id;
        const name = data.displayName;
        const picture = MediaServer.profiles(id);

        // Static elements for self
        if(this.id === id){
            // Main page
            document.getElementById("user-name").innerText = name;
            document.getElementById("user-picture").src = picture;
            // User settings
            document.getElementById('settings-user-name').value = name;
            document.getElementById('settings-user-picture').src = picture;
        }
         
        // Dynamic elements
        for (const icon of document.getElementsByName(`user-picture-${id}`)) {
            icon.src = picture;
        }
        for (const name of document.getElementsByName(`user-name-${id}`)) {
            name.innerText = data.displayName;
        }
    }

    isAdmin(){
        return (this.#type == "ADMIN");
    }

    /** @param {UserStatusUpdate} data */
    setStatus(data){
        const id = data.userId;
        const status = data.status;

        const color = statusToColor(status);

        // Static elements for self
        if(this.id === id){
            document.getElementById("user-dot").setAttribute('color', color);
        }
    }

    logout(){
        CoreServer.fetch(`/auth/logout`, 'GET').then(() => {
            sessionStorage.removeItem('lastState');
            localStorage.removeItem('userSettings');
            eraseCookie('jwtToken');
            document.location.href = `index.html`;
        });
    }
}