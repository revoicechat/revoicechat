/** @abstract */
export default class RoomController {
    /** @type {TextController} */
    textController;
    /** @type {string} */
    id;
    /** @type {string} */
    name;
    /** @type {string} */
    type;

    /** @param {UserController} user */
    constructor(user) {
        this.user = user;
    }
}