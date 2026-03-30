import Streamer from "./stream.streamer.js";
import Viewer from "./stream.viewer.js";
import { i18n } from "../../lib/i18n.js";
import CoreServer from "../core/core.server.js";
import ReVoiceChat from "../revoicechat.js";
import Codec from "../utils/codec.js";
import Modal from "../../component/modal.component.js";

export default class StreamController {
    #streamer = {};
    #viewer = [];
    #room;
    #user;
    #webcamEnabled = false;
    #displayEnabled = false;
    #contextMenu;

    constructor(user, room) {
        this.#room = room;
        this.#user = user;
        this.#contextMenu = document.getElementById('stream-context-menu');
    }

    attachEvents() {
        document.getElementById("stream-webcam").onclick = () => this.#toggleStream("webcam");
        document.getElementById("stream-display").onclick = () => this.#toggleStream("display");
        window.addEventListener("beforeunload", async () => {
            await this.stopAll()
        })
    }

    #toggleStream(type) {
        if (type == "webcam") {
            if (this.#webcamEnabled) {
                this.#stopStream("webcam");
            } else {
                this.#startWebcam();
            }
            return;
        }

        if (type == "display") {
            if (this.#displayEnabled) {
                this.#stopStream("display");
            } else {
                this.#startDisplay();
            }
        }
    }

    async #startDisplay() {
        try {
            const div = document.createElement('div');
            this.#streamer["display"] = {
                stream: new Streamer(CoreServer.streamUrl(), this.#user, ReVoiceChat.getToken()),
                div: div
            }

            let resolution = 1;
            let framerate = 1;
            let codec = 'AUTO';
            let bitrate = 3;
            Modal.toggle({
                title: i18n.translateOne("stream.modal.title"),
                showCancelButton: true,
                focusConfirm: false,
                confirmButtonText: i18n.translateOne("stream.modal.confirm"),
                width: "35rem",
                html: `
                        <form class='popup' id='popup-stream'>
                            <label data-i18n="stream.modal.resolution">Resolution</label>
                            <input id='popup-resolution' type='range' list='datalist-resolution' step='1' min='0' max='3' value='1'>
                            <datalist id="datalist-resolution">
                                <option value='0' label='720p'></option>
                                <option value='1' label='1080p'></option>
                                <option value='2' label='1440p'></option>
                                <option value='3' label='2160p'></option>
                            </datalist>

                            <label data-i18n="stream.modal.framerate">Framerate</label>
                            <input id='popup-framerate' type='range' list='datalist-framerate' step='1' min='0' max='3' value='1'>
                            <datalist id='datalist-framerate'>
                                <option value='0'>10</option>
                                <option value='1'>30</option>
                                <option value='2'>60</option>
                                <option value='3'>120</option>
                            </datalist>

                            <label data-i18n="stream.modal.bitrate">Bitrate (Mbps)</label>
                            <input id='popup-bitrate' type='range' list='datalist-bitrate' step='1' min='1' max='10' value='3'>
                            <datalist id='datalist-bitrate'>
                                <option value='1'>1</option>
                                <option value='2'>2</option>
                                <option value='3'>3</option>
                                <option value='4'>4</option>
                                <option value='5'>5</option>
                                <option value='6'>6</option>
                                <option value='7'>7</option>
                                <option value='8'>8</option>
                                <option value='9'>9</option>
                                <option value='10'>10</option>
                            </datalist>

                            <label data-i18n="stream.modal.codec">Codec</label>
                            <select id='popup-codec'>
                                <option value='AUTO'>Auto</option>
                                <option value='VP9'>${i18n.translateOne("stream.modal.vp9")}</option>
                                <option value='AV1'>${i18n.translateOne("stream.modal.av1")}</option>
                            </select>
                        </form>`,
                didOpen: () => {
                    i18n.translatePage(document.getElementById("popup-stream"))
                    document.getElementById('popup-resolution').oninput = () => { resolution = document.getElementById('popup-resolution').value };
                    document.getElementById('popup-framerate').oninput = () => { framerate = document.getElementById('popup-framerate').value };
                    document.getElementById('popup-codec').oninput = () => { codec = document.getElementById('popup-codec').value };
                    document.getElementById('popup-bitrate').oninput = () => { bitrate = document.getElementById('popup-bitrate').value };
                }
            }).then(async (result) => {
                if (result.isConfirmed) {
                    const codecConfig = await Codec.streamConfig(resolution, framerate, codec, bitrate);

                    if (codecConfig) {
                        const player = await this.#streamer["display"].stream.start("display", codecConfig);

                        div.className = "player";
                        div.appendChild(player);
                        div.onclick = () => {
                            this.focus(div)
                        }
                        div.oncontextmenu = (event) => {
                            event.preventDefault();
                        }
                        document.getElementById('stream-container').appendChild(div);

                        this.#displayEnabled = true;
                        document.getElementById("stream-display").classList.add("green");
                    }
                }
            });
        }
        catch (error) {
            console.error(error);
            await Modal.toggleError(i18n.translateOne("stream.start.error"));
        }
    }

    async #startWebcam() {
        try {
            const div = document.createElement('div');
            this.#streamer["webcam"] = {
                stream: new Streamer(CoreServer.streamUrl(), this.#user, ReVoiceChat.getToken()),
                div: div
            }
            const player = await this.#streamer["webcam"].stream.start("webcam", await Codec.webcamConfig());
            div.className = "player";
            div.appendChild(player);
            div.onclick = () => {
                this.focus(div)
            }
            div.oncontextmenu = (event) => {
                event.preventDefault();
            }
            document.getElementById('stream-container').appendChild(div);
            this.#webcamEnabled = true;
            document.getElementById("stream-webcam").classList.add("green");
        }
        catch (error) {
            console.error(error);
            await Modal.toggleError(i18n.translateOne("stream.start.error"));
        }
    }

    async #stopStream(type) {
        try {
            if (this.#streamer[type]) {
                await this.#streamer[type].stream.stop();
                this.#streamer[type].div.remove();
                this.#streamer[type] = null;

                if (type === "webcam") {
                    this.#webcamEnabled = false;
                    document.getElementById("stream-webcam").classList.remove("green");

                }

                if (type === "display") {
                    this.#displayEnabled = false;
                    document.getElementById("stream-display").classList.remove("green");
                }
            }
        } catch (error) {
            console.error(error);
            await Modal.toggleError(i18n.translateOne("stream.start.error"));
        }
    }

    /**
     * @param {StreamingRepresentation|StreamRepresentation} stream
     * @return {Promise<void>}
     */
    async joinModal(stream) {
        const userId = stream.user;
        const streamName = stream.streamName;

        const userExtension = document.getElementById(`voice-user-extension-${userId}`);
        if (userExtension) {
            if (streamName === "webcam") {
                document.getElementById(`voice-user-extension-webcam-${userId}`).classList = "green";
            }
            if (streamName === "display") {
                document.getElementById(`voice-user-extension-display-${userId}`).classList = "green";
            }
        }

        if (this.#room.voiceController.getActiveRoom() && this.#user.id != userId && !this.#viewer[`${userId}-${streamName}`]) {
            const displayName = (await CoreServer.fetch(`/user/${userId}`)).displayName;
            const streamContainter = document.getElementById('stream-container');
            const modal = document.createElement('div');
            modal.id = `stream-modal-${userId}-${streamName}`;
            modal.className = "player join";
            modal.dataset.i18n = "stream.join.button"
            modal.dataset.i18nValue = displayName
            modal.innerText = i18n.translateOne(modal.dataset.i18n, [displayName])
            modal.onclick = () => {
                modal.remove();
                this.join(userId, streamName)
            }
            streamContainter.appendChild(modal);
        }
    }

    removeModal(userId, streamName) {
        const modal = document.getElementById(`stream-modal-${userId}-${streamName}`);
        if (modal) {
            modal.remove();
        }
    }

    async join(userId, streamName) {
        if (this.#room.voiceController.getActiveRoom() && userId != this.#user.id) {
            const div = document.createElement('div');

            this.#viewer[`${userId}-${streamName}`] = {
                stream: new Viewer(CoreServer.streamUrl(), ReVoiceChat.getToken(), this.#user.settings),
                div: div
            }

            const stream = this.#viewer[`${userId}-${streamName}`].stream;
            const video = await stream.join(userId, streamName);

            div.className = "player";
            div.appendChild(video);
            div.onclick = () => {
                this.focus(div)
            }
            div.oncontextmenu = (event) => {
                event.preventDefault();
                this.#contextMenu.load(stream, this, userId, streamName);
                this.#contextMenu.open(event.clientX, event.clientY)
            }

            // Streamer container
            document.getElementById('stream-container').appendChild(div);
        }
    }

    /**
     * @param {StreamingRepresentation} stream
     * @return {Promise<void>}
     */
    async leave(stream) {
        const userId = stream.user;
        const streamName = stream.name;

        const userExtension = document.getElementById(`voice-user-extension-${userId}`);
        if (userExtension) {
            if (streamName === "webcam") {
                document.getElementById(`voice-user-extension-webcam-${userId}`).classList = "hidden";
            }
            if (streamName === "display") {
                document.getElementById(`voice-user-extension-display-${userId}`).classList = "hidden";
            }
        }

        if (this.#room.voiceController.getActiveRoom() && userId != this.#user.id) {
            if (this.#viewer[`${userId}-${streamName}`]) {
                await this.#viewer[`${userId}-${streamName}`].stream.leave();
                this.#viewer[`${userId}-${streamName}`].div.remove();
                this.#viewer[`${userId}-${streamName}`] = null;

                const streamContainter = document.getElementById('stream-container');
                streamContainter.className = "stream";
                for (const child of streamContainter.childNodes) {
                    child.classList.remove("hidden");
                }
            } else {
                this.removeModal(userId, streamName);
            }
        }

        this.availableStream(this.#room.voiceController.getActiveRoom());
    }

    async stopAll() {
        // Stop streaming
        document.getElementById("stream-webcam").classList.remove("green");
        document.getElementById("stream-display").classList.remove("green");
        this.#stopStream("webcam");
        this.#stopStream("display");
        this.#displayEnabled = false;
        this.#webcamEnabled = false;

        // Stop watching
        for (const key of Object.keys(this.#viewer)) {
            if (this.#viewer[key]) {
                await this.#viewer[key].stream.leave();
                this.#viewer[key].div.remove();
                this.#viewer[key] = null;
            }
        }
    }

    removeAll() {
        document.getElementById('stream-container').innerHTML = "";
    }

    removeAllExceptWatching() {
        const children = document.getElementById('stream-container').childNodes;
        for (const child of children) {
            if (child.className === "player join") {
                child.remove();
            }
        }
    }

    async availableStream(roomId) {
        /** @type {RoomPresence} */
        const result = await CoreServer.fetch(`/room/${roomId}/user`, 'GET');

        if (!result?.connectedUser) {
            console.debug("Stream : No user in room");
            return;
        }

        this.removeAllExceptWatching();

        for (const user of result.connectedUser) {
            for (const stream of user.streams) {
                this.joinModal(stream);
            }
        }
    }

    focus(element) {
        for (const child of element.parentElement.children) {
            child.classList.add("hidden");
        }
        element.classList.remove("hidden");
        element.parentElement.classList.add("fullscreen");
        element.onclick = () => {
            this.unfocus(element);
        }
    }

    unfocus(element) {
        for (const child of element.parentElement.children) {
            child.classList.remove("hidden");
        }
        element.parentElement.classList.remove("fullscreen");
        element.onclick = () => {
            this.focus(element);
        }
    }
}