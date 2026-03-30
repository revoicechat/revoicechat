import CoreServer from './core/core.server.js';
import State from './state.js';
import Alert from './utils/alert.js';
import Router from './router.js';
import UserController from './user.controller.js';
import PublicRoom from './public.room.controller.js';
import ServerController from './server.controller.js';
import MobileController from "./utils/mobile.js";
import { reloadEmojis } from './emoji.js';
import { Sse } from "./core/sse.js";
import { getCookie, getQueryVariable, initTools } from "../lib/tools.js";
import '../component/components.js';
import { i18n } from "../lib/i18n.js";
import MediaServer from "./media/media.server.js";
import AdminSettingsController from './admin/admin.settings.controller.js';

export default class ReVoiceChat {
    /** @type {string} */
    static #token;
    /** @type {Router} */
    router = new Router();
    /** @type {UserController} */
    user;
    /** @type {PublicRoom} */
    room;
    /** @type {ServerController} */
    server;
    /** @type {State} */
    state;
    /** @type {Sse} */
    #sse;

    adminSettings;

    constructor() {
        initTools();
        // Retrieve URL
        const storedCoreUrl = localStorage.getItem("lastHost");
        if (!storedCoreUrl) {
            document.location.href = `index.html`;
        }

        // Validate and store URL
        const core = new URL(storedCoreUrl);
        MediaServer.init(core, ReVoiceChat.getToken());
        CoreServer.init(core, ReVoiceChat.getToken());

        // Instantiate other classes
        this.user = new UserController();
        this.room = new PublicRoom(this.user);
        this.server = new ServerController(this.room, this.router, this.user);
        this.state = new State(this);
        this.adminSettings = new AdminSettingsController(this.user);

        // Add missing classes
        this.user.settings.setRoom(this.room);

        this.#sse = CoreServer.sse((data) => this.#handleSSEMessage(data), () => this.#handleSSEError())
        /** @type {SSEHandlers} */
        this.sseHandlers = new SSEHandlers(this);

        // Save state before page unload
        window.addEventListener("beforeunload", () => {
            this.state.save();
            this.#sse.closeSSE();
        })

        // Load more when document is fully loaded
        document.addEventListener('DOMContentLoaded', () => this.#load());
    }

    async #load() {
        this.state.load();
        await this.user.load();
        await i18n.translate(this.user.settings.getLanguage());
        await this.server.load();
        await this.#sse.openSSE();
        this.room.attachEvents();
        MobileController.load();
        Alert.attachEvents();
        this.user.settings.buildMessageExemple();
        this.router.routeTo(getQueryVariable('r'));

        if (this.user.isAdmin()) {
            await this.adminSettings.load();
        }
    }

    /** @return {string} Token */
    static getToken() {
        if (!ReVoiceChat.#token) {
            const storedToken = getCookie("jwtToken");
            if (storedToken) {
                ReVoiceChat.#token = storedToken;
            } else {
                document.location.href = `index.html`;
            }
        }
        return ReVoiceChat.#token;
    }

    #handleSSEMessage(data) {
        try {
            const event = JSON.parse(data);
            console.debug("SSE : ", event);
            this.sseHandlers.handle(event.type, event.data);
        } catch (error) {
            console.error('Error parsing SSE message:', error, data);
        }
    }

    #handleSSEError() {
        console.error(`An error occurred while attempting to connect to "/api/sse".\nRetry in 10 seconds`);
        setTimeout(() => {
            this.#sse.openSSE();
            void this.room.textController.load(this.room.id);
        }, 10000);
    }

    userSettings() {
        return this.user.settings
    }

    /** @param {import('./types.js').ProfilPictureUpdate} data */
    updateAllPicture(data) {
        const time = Date.now();
        for (const picture of document.querySelectorAll(`img[data-id="${data.id}"]`)) {
            picture.src = MediaServer.profiles(data.id, time);
        }
    }
}

class SSEHandlers {
    /** @param {ReVoiceChat} context */
    constructor(context) {
        this.context = context;
        this.server = context.server;
        this.room = context.room;
        this.user = context.user;

        this.handlers = {
            'PING': () => { },
            'SERVER_UPDATE': (data) => this.server.update(data),
            'ROOM_UPDATE': (data) => this.room.update(data, this.server.id),
            'ROOM_MESSAGE': (data) => this.room.textController.message(data),
            'DIRECT_MESSAGE': () => { },
            'NEW_USER_IN_SERVER': (data) => this.server.updateUserInServer(data),
            'USER_STATUS_UPDATE': async (data) => { this.user.setStatus(data); await this.room.loadUsers(); },
            'USER_UPDATE': (data) => this.user.update(data),
            'VOICE_JOINING': (data) => this.room.voiceController.userJoining(data),
            'VOICE_LEAVING': (data) => this.room.voiceController.userLeaving(data),
            'EMOTE_UPDATE': () => reloadEmojis(),
            'RISK_MANAGEMENT': () => this.server.settings.riskModify(),
            'STREAM_START': (data) => this.room.voiceController?.streamController?.joinModal(data),
            'STREAM_STOP': (data) => this.room.voiceController?.streamController?.leave(data),
            'PROFIL_PICTURE_UPDATE': (data) => this.context.updateAllPicture(data)
        };
    }

    handle(type, data) {
        const handler = this.handlers[type];
        if (handler) {
            handler(data);
        } else {
            console.warn("SSE type unknown: ", type);
        }
    }
}

globalThis.RVC = new ReVoiceChat();

document.addEventListener('contextmenu', (e) => e.preventDefault());
