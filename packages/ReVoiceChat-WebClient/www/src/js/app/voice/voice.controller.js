import Alert from "../utils/alert.js";
import VoiceCall from "./voice.js";
import StreamController from '../stream/stream.controller.js';
import MediaServer from "../media/media.server.js";
import CoreServer from "../core/core.server.js";
import ReVoiceChat from "../revoicechat.js";
import Modal from "../../component/modal.component.js";
import { i18n } from "../../lib/i18n.js";

export default class VoiceController {
    /** @type {VoiceCall|null} */
    #voiceCall;
    /** @type {string|null} */
    #activeRoom;
    /** @type {UserController} */
    #user;
    /** @type {Room} */
    #room;
    /** @type {VoiceContextMenu} */
    #contextMenu;
    #lastStateSelfMute = false;
    /** @type {StreamController} */
    streamController;

    #cachedElement = {
        voiceSelfMute: null,
        usersGlow: [],
    }

    /**
     * @param {UserController} user
     * @param {Room} room
     */
    constructor(user, room) {
        this.#user = user;
        this.#room = room;
        this.streamController = new StreamController(user, room);
        this.#contextMenu = document.getElementById('voice-context-menu');
        this.#cachedElement.voiceSelfMute = document.getElementById(`voice-self-mute`);
    }

    attachEvents() {
        document.getElementById("voice-self-mute").addEventListener('click', async () => await this.#toggleSelfMute());
        document.getElementById("voice-self-deaf").addEventListener('click', async () => await this.#toggleSelfDeaf());
        this.streamController.attachEvents();
    }

    getActiveRoom() {
        return this.#activeRoom;
    }

    // <user> call this to join a call in a room
    async join(roomId) {
        if (this.#activeRoom) {
            if (this.#activeRoom !== roomId) {
                await this.leave(this.#activeRoom);
            }
            else {
                return;
            }
        }

        this.#activeRoom = roomId;

        try {
            this.#voiceCall = new VoiceCall(this.#user);
            await this.#voiceCall.open(CoreServer.voiceUrl(), roomId, ReVoiceChat.getToken(), this, (reason) => this.#voiceError(reason));

            // Update users in room
            await this.#updateJoinedUsers();

            // Update self
            this.updateSelf(this.#user.settings.voice);

            // Update counter
            this.#updateUserCounter(roomId);

            // Update context menu
            this.#contextMenu.setVoiceCall(this.#voiceCall);

            // Check for available stream
            this.streamController.availableStream(roomId);

            // Audio alert
            Alert.play('voiceConnected');
        }
        catch (error) {
            console.error(error);
            await this.#voiceError(error);
        }

        // Prompt user before reloading
        window.addEventListener("beforeunload", (event) => {
            event.preventDefault();
            return false;
        })
    }

    /**
     * @param {string} errorMessage
     * @returns {Promise<void>}
     */
    async #voiceError(errorMessage) {
        await Modal.toggleError("Can't join voicechat", errorMessage);
        await this.leave(false);
    }

    // <user> call this to leave a call in a room
    async leave(playAlert = true) {
        await this.#voiceCall.close();
        await this.streamController.stopAll();
        this.streamController.removeAll();
        this.updateSelf();
        await this.#updateJoinedUsers();
        this.#updateUserCounter(this.#activeRoom);
        this.#activeRoom = null;
        this.#voiceCall = null;

        // Update context menu
        this.#contextMenu.setVoiceCall(null);

        // Remove controls if current room is not voice
        if (this.#room.type !== "VOICE") {
            document.getElementById("voice-control-panel").classList.add('hidden');
        }

        // Audio alert
        if (playAlert) {
            Alert.play('voiceDisconnected');
        }
    }

    /**
     * <server.js> call this when a new user join the room
     * @param {VoiceJoiningNotification} data
     * @return {Promise<void>}
     */
    async userJoining(data) {
        this.#updateUserCounter(data.roomId);

        const userData = data.user;
        const voiceContent = document.getElementById(`voice-users-${data.roomId}`);
        voiceContent.appendChild(this.#createUserElement({ user: userData, streams: [] }));

        // NOT our room
        if (data.roomId !== this.#room.id) { return; }

        // User joining this is NOT self and current user is connected to voice room
        if (userData.id !== this.#user.id && this.#voiceCall && this.#voiceCall.getState() === VoiceCall.OPEN) {
            this.#updateUserControls(userData.id);
            Alert.play('voiceUserJoin');
        }
    }

    /**
     * <server.js> call this when a user leave the room
     * @param {VoiceLeavingNotification} data
     * @return {Promise<void>}
     */
    async userLeaving(data) {
        this.#updateUserCounter(data.roomId);

        const userId = data.user;

        // Remove user from UI
        const userElement = document.getElementById(`voice-${userId}`)
        if (userElement) {
            userElement.remove();
        }

        // NOT our room
        if (data.roomId !== this.#room.id) { return; }

        // User leaving is NOT self
        if (userId !== this.#user.id && this.#voiceCall && this.#voiceCall.getState() === VoiceCall.OPEN) {
            this.#voiceCall.removeUser(userId);
            Alert.play('voiceUserLeft');
        }
    }

    // Show users in a room
    async showJoinedUsers(roomId, voiceContent) {
        const result = await CoreServer.fetch(`/room/${roomId}/user`, 'GET');

        if (!result || result.connectedUser.length == 0) {
            console.debug(`No user in voice room: ${roomId}`);
            return;
        }

        const connectedUser = result.connectedUser;

        const sortedByDisplayName = [...connectedUser].sort((a, b) => {
            return a.user.displayName.localeCompare(b.user.displayName);
        });

        voiceContent.innerHTML = "";
        for (const connectedUser of sortedByDisplayName) {
            voiceContent.appendChild(this.#createUserElement(connectedUser));
        }

        // Room is currently active
        if (this.#activeRoom === roomId) {
            this.#updateJoinedUsers();
        }
    }

    setUserGlow(userId, enabled) {
        if (!this.#cachedElement.usersGlow[userId]) {
            this.#cachedElement.usersGlow[userId] = document.getElementById(`voice-gate-${userId}`)
        }

        if (enabled) {
            this.#cachedElement.usersGlow[userId].classList.add('active');
        }
        else {
            this.#cachedElement.usersGlow[userId].classList.remove('active');
        }
    }

    setSelfGlow(enabled) {
        if (enabled) {
            this.#cachedElement.voiceSelfMute.classList.add('green');
        }
        else {
            this.#cachedElement.voiceSelfMute.classList.remove('green');
        }
    }

    // Create DOM Element / HTML for a given user
    #createUserElement(connectedUser) {
        const userId = connectedUser.user.id;
        const userName = connectedUser.user.displayName
        const profilePicture = MediaServer.profiles(userId);

        let webcamEnable = false;
        let displayEnable = false;

        for (const stream of connectedUser.streams) {
            if (stream.streamName === "webcam") {
                webcamEnable = true;
            }
            if (stream.streamName === "display") {
                displayEnable = true;
            }
        }

        // Extension
        const extension = document.createElement('div');
        extension.className = "extension";
        extension.id = `voice-user-extension-${userId}`;

        // Extension : Webcam
        const extensionWebcam = document.createElement('revoice-icon-camera');
        extensionWebcam.id = `voice-user-extension-webcam-${userId}`;
        extensionWebcam.dataset.i18nTitle = "voice.extention.webcam";
        extensionWebcam.className = webcamEnable ? "green" : "hidden";
        extension.appendChild(extensionWebcam);

        // Extension : Display
        const extensionDisplay = document.createElement('revoice-icon-display');
        extensionDisplay.id = `voice-user-extension-display-${userId}`;
        extensionDisplay.dataset.i18nTitle = "voice.extention.display"
        extensionDisplay.className = displayEnable ? "green" : "hidden";
        extension.appendChild(extensionDisplay);

        // Extension : Self Mute
        const extensionSelfMute = document.createElement('revoice-icon-microphone');
        extensionSelfMute.id = `voice-user-extension-self-mute-${userId}`;
        extensionSelfMute.dataset.i18nTitle = "voice.extention.mute"
        extensionSelfMute.className = "hidden";
        extension.appendChild(extensionSelfMute);

        // Extension : Local Mute
        const extensionMute = document.createElement('revoice-icon-speaker-x');
        extensionMute.id = `voice-user-extension-mute-${userId}`;
        extensionMute.dataset.i18nTitle = "voice.extention.mute"
        extensionMute.className = this.#user.settings.voice.users[userId]?.muted ? "red" : "hidden";
        extension.appendChild(extensionMute);

        const DIV = document.createElement('div');
        DIV.id = `voice-${userId}`;
        DIV.className = "voice-profile";
        DIV.innerHTML = `
            <div class='block-user gate' id='voice-gate-${userId}'>
                <div class='relative'>
                    <img src='${profilePicture}' alt='PFP' class='icon' data-id="${userId}" name='user-picture-${userId}'/>
                </div>
                <div class='user'>
                    <div class='name' name='user-name-${userId}'>${userName}</div>
                </div>
            </div>
        `;

        DIV.appendChild(extension);

        // Context menu
        if (userId === this.#user.id) {
            DIV.addEventListener('contextmenu', (event) => {
                event.preventDefault();
            }, false);
        } else {
            DIV.addEventListener('contextmenu', (event) => {
                event.preventDefault();
                this.#contextMenu.load(this.#user.settings, userId, this);
                this.#contextMenu.open(event.clientX, event.clientY);
            }, false);
        }

        return DIV;
    }

    // <voiceUpdateJoinedUsers> and <voiceUserJoining> call this to update control on given user
    #updateUserControls(userId) {
        switch (this.#voiceCall.getState()) {
            case VoiceCall.CLOSE: {
                if (document.getElementById(`voice-controls-${userId}`) !== null) {
                    document.getElementById(`voice-controls-${userId}`).remove();
                }
                break;
            }
            case VoiceCall.OPEN: {
                if (document.getElementById(`voice-controls-${userId}`) !== null) {
                    console.info('VOICE : There is already controls in this room');
                    break;
                }

                if (!this.#user.settings.voice.users[userId]) {
                    this.#user.settings.voice.users[userId] = { volume: 1, muted: false };
                }

                break;
            }
        }
    }

    updateUserExtension(userId) {
        const userExtension = document.getElementById(`voice-user-extension-${userId}`);
        if (userExtension) {
            // User muted
            if (this.#user.settings.voice.users[userId]?.muted) {
                document.getElementById(`voice-user-extension-mute-${userId}`).classList = "red";
            }
            else {
                document.getElementById(`voice-user-extension-mute-${userId}`).classList = "hidden";
            }
        }
    }

    // <user> call this to mute himself
    async #toggleSelfMute() {
        if (this.#voiceCall) {
            await this.#voiceCall.toggleSelfMute();
            await this.#updateSelfMute(true);
            this.#saveSettings();
        }
    }

    async #updateSelfMute(alert = true) {
        const muteButton = document.getElementById("voice-self-mute");
        if (this.#voiceCall) {
            if (await this.#voiceCall.getSelfMute()) {
                // Muted
                muteButton.classList.add('red');
                if (alert) {
                    Alert.play('microphoneMuted');
                }
            }
            else {
                // Unmuted
                muteButton.classList.remove('red');
                if (alert) {
                    Alert.play('microphoneActivated');
                }
            }
        }
    }

    setSelfVolume() {
        if (this.#voiceCall) {
            this.#voiceCall.setSelfVolume(this.#user.settings.voice.self.volume);
        }
    }

    async #toggleSelfDeaf() {
        if (this.#voiceCall) {
            await this.#voiceCall.toggleSelfDeaf();
            if (await this.#voiceCall.getSelfDeaf()) {
                this.#lastStateSelfMute = await this.#voiceCall.getSelfMute();
                await this.#voiceCall.setSelfMute(true);
            } else {
                await this.#voiceCall.setSelfMute(this.#lastStateSelfMute);
            }
            await this.#updateSelfDeaf(true);
            await this.#updateSelfMute(false);
            this.#saveSettings();
        }
    }

    async #updateSelfDeaf(alert = true) {
        const button = document.getElementById("voice-self-deaf");
        if (this.#voiceCall) {
            if (await this.#voiceCall.getSelfDeaf()) {
                // Muted
                button.classList.add('red');
                if (alert) {
                    Alert.play('soundMuted');
                }
            }
            else {
                // Unmuted
                button.classList.remove('red');
                if (alert) {
                    Alert.play('soundActivated');
                }
            }
        }
    }

    setOutputVolume(value) {
        if (this.#voiceCall) {
            this.#voiceCall.setOutputVolume(value);
        }
    }

    #saveSettings() {
        if (this.#voiceCall) {
            this.#user.settings.voice = this.#voiceCall.getSettings();
        }
        this.#user.settings.save();
    }

    updateGate() {
        if (this.#voiceCall) {
            this.#voiceCall.setGate(this.#user.settings.voice.gate);
        }
    }

    // Update user controls and UI
    updateSelf(voiceSettings = null) {
        const voiceAction = document.getElementById("voice-join-action");
        const muteButton = document.getElementById("voice-self-mute");
        const deafButton = document.getElementById("voice-self-deaf");
        const webcamButton = document.getElementById("stream-webcam");
        const displayButton = document.getElementById("stream-display");
        const instanceState = this.#voiceCall ? this.#voiceCall.getState() : VoiceCall.CLOSE;

        switch (instanceState) {
            case VoiceCall.CONNECTING:
                // Set disconnect actions
                voiceAction.className = "join";
                voiceAction.classList.add('waiting');
                voiceAction.title = "Waiting to join the room";
                voiceAction.innerHTML = `<revoice-icon-phone-x></revoice-icon-phone-x>`;
                voiceAction.onclick = () => this.leave();
                break;

            case VoiceCall.CLOSE:
                // Set connect actions
                if (this.#activeRoom) {
                    document.getElementById(this.#activeRoom).classList.remove('active-voice');
                }
                voiceAction.className = "join";
                voiceAction.classList.add('disconnected');
                voiceAction.title = "Join the room";
                voiceAction.innerHTML = `<revoice-icon-phone></revoice-icon-phone>`;
                voiceAction.onclick = () => this.join(this.#room.id);
                muteButton.classList.add('hidden');
                deafButton.classList.add('hidden');
                webcamButton.classList.add('hidden');
                displayButton.classList.add('hidden');
                break;

            case VoiceCall.OPEN:
                document.getElementById(this.#activeRoom).classList.add('active-voice');
                voiceAction.className = "join";
                voiceAction.classList.add('connected');
                voiceAction.title = "Leave the room";
                voiceAction.innerHTML = `<revoice-icon-phone-x></revoice-icon-phone-x>`;
                voiceAction.onclick = () => this.leave();
                muteButton.classList.remove('hidden');
                deafButton.classList.remove('hidden');
                webcamButton.classList.remove('hidden');
                displayButton.classList.remove('hidden');
                if (voiceSettings) {
                    if (voiceSettings.muted) {
                        muteButton.classList.add('red');
                    }
                    if (voiceSettings.deaf) {
                        deafButton.classList.add('red');
                    }
                }
                this.#updateSelfDeaf(false);
                this.#updateSelfMute(false);
                break;
        }
    }

    updateJoinButton(roomId) {
        if (!this.#activeRoom) {
            document.getElementById("voice-join-action").onclick = () => this.join(roomId);
        }
    }

    // Count user in room
    async usersCount(roomId) {
        const result = await CoreServer.fetch(`/room/${roomId}/user`, 'GET');
        return result?.connectedUser ? result.connectedUser.length : 0;
    }

    // Update users counter
    async #updateUserCounter(roomId) {
        const count = await this.usersCount(roomId);
        const element = document.getElementById(`room-extension-${roomId}`);
        element.innerHTML = `${count}<revoice-icon-user></revoice-icon-user>`
    }

    // Add or remove controls on users in room
    async #updateJoinedUsers() {
        const result = await CoreServer.fetch(`/room/${this.#room.id}/user`, 'GET');

        if (result === null) {
            console.debug("VOICE : No user in room");
            return;
        }

        const connectedUsers = result.connectedUser;

        for (const connectedUser of connectedUsers) {
            const userId = connectedUser.id;

            // Not self
            if (this.#user.id !== userId) {
                this.#updateUserControls(userId);
                this.updateUserExtension(userId);
            }
        }
    }

    isCallActive() {
        return !!this.#activeRoom;
    }
}
