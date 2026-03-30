import Alert from './utils/alert.js';
import {humanFileSize, sanitizeString, timestampToText} from "../lib/tools.js";
import {i18n} from "../lib/i18n.js";
import MediaServer from "./media/media.server.js";
import CoreServer from "./core/core.server.js";
import Modal from "../component/modal.component.js";
import {emojiPicker} from "./emoji.js";
import {renderEmojis} from "../component/emoji.component.js";

export default class TextController {
    static MODE_SEND = 0;
    static MODE_EDIT = 1;

    mode = 0;

    /** @type {UserController} */
    #user;
    /** @type {Room} */
    #room;
    /** @type {string|null} */
    #editId;
    #attachmentMaxSize = 0;
    #cachedRooms = {};
    /** @type {MessageRepresentation|null} */
    #repliedMessage = null;
    /** @type {MutationObserver|null} */
    #observer = null;

    #core = {
        send: null,
        load: null,
        loadMore: null
    }
    #elements = {
        cacheContainer: null,
        textReplyMessage: null,
        textInput: null,
        textAttachment: null,
        textAttachmentDiv: null,
        attachmentsAdd: null,
        attachmentsRemove: null,
    }

    /**
     * @param {UserController} user
     * @param {Room} room
     */
    constructor(user, room, privateRoom = false) {
        this.#user = user;
        this.#room = room;
        if (privateRoom) {
            this.#elements.cacheContainer = document.getElementById("private-cache-container");
            this.#elements.textReplyMessage = document.getElementById("private-text-reply-message");
            this.#elements.textInput = document.getElementById("private-text-input");
            this.#elements.textAttachment = document.getElementById("private-text-attachment");
            this.#elements.textAttachmentDiv = document.getElementById("private-text-attachment-div");
            this.#elements.attachmentsAdd = document.getElementById("private-attachment-button-add");
            this.#elements.attachmentsRemove = document.getElementById("private-attachment-button-remove");
            this.#core.send = async (roomId, data) => { return await CoreServer.fetch(`/private-message/${roomId}/message`, 'PUT', data) }
            this.#core.load = async (roomId) => { return await CoreServer.fetch(`/private-message/${roomId}/message`, 'GET') }
            this.#core.loadMore = async (roomId, firstMessageId) => { return await CoreServer.fetch(`/private-message/${roomId}/message?lastMessage=${firstMessageId}`, 'GET') }
        }
        else {
            this.#elements.cacheContainer = document.getElementById("cache-container");
            this.#elements.textReplyMessage = document.getElementById("text-reply-message");
            this.#elements.textInput = document.getElementById("text-input");
            this.#elements.textAttachment = document.getElementById("text-attachment");
            this.#elements.textAttachmentDiv = document.getElementById("text-attachment-div");
            this.#elements.attachmentsAdd = document.getElementById("attachment-button-add");
            this.#elements.attachmentsRemove = document.getElementById("attachment-button-remove");
            this.#core.send = async (roomId, data) => { return await CoreServer.fetch(`/room/${roomId}/message`, 'PUT', data) }
            this.#core.load = async (roomId) => { return await CoreServer.fetch(`/room/${roomId}/message`, 'GET') }
            this.#core.loadMore = async (roomId, firstMessageId) => { return await CoreServer.fetch(`/room/${roomId}/message?lastMessage=${firstMessageId}`, 'GET'); }
        }
        this.#observeReply();
    }

    /** Setup observer for replied container */
    #observeReply() {
        this.#observer?.disconnect();
        this.#observer = new MutationObserver((mutations) => {
            for (const mutation of mutations) {
                if (mutation.type === 'attributes' && mutation.attributeName === 'data-message-id') {
                    this.#showRepliedMessage(this.#elements.textReplyMessage);
                }
            }
        });
        this.#observer.observe(this.#elements.textReplyMessage, {
            attributes: true,
            attributeFilter: ['data-message-id']
        });
    }

    /** @param {HTMLElement} element */
    #showRepliedMessage(element) {
        element.innerHTML = ""
        if (this.#repliedMessage) {
            element.classList.remove("hidden");
            const message = document.createElement("div");
            message.innerHTML = `<span data-i18n="message.answer.to" class="no-wrap-text">Reply to</span>
                                 <span> </span>
                                 <span style="font-weight: bold">${this.#repliedMessage.user.displayName}</span>`;
            message.style.width = "100%";
            message.style.fontSize = "0.8rem";
            i18n.translatePage(message)
            const closeButton = document.createElement("div");
            closeButton.innerHTML = "<button><revoice-icon-circle-x></revoice-icon-circle-x></button>";
            closeButton.onclick = () => this.#removeRepliedMessage();
            element.appendChild(message);
            element.appendChild(closeButton);
        } else {
            element.classList.add("hidden");
        }
    }

    #removeRepliedMessage() {
        this.#repliedMessage = null;
        this.#elements.textReplyMessage.dataset.messageId = "";
    }

    attachEvents() {
        this.#elements.textInput.addEventListener('keydown', async (event) => await this.#eventHandler(event));
        this.#elements.textInput.addEventListener('oninput', () => this.oninput(this.#elements.textInput));
        this.#elements.textInput.addEventListener('paste', (event) => this.#pasteHandler(event));
        this.#elements.attachmentsAdd.addEventListener('click', () => this.#addAttachment());
        this.#elements.attachmentsRemove.addEventListener('click', () => this.#removeAttachment());
        this.#elements.cacheContainer.addEventListener("scroll", () => {
            const element = this.#cachedRooms[this.#room.id];
            if (element) {
                element.scrollTop = this.#elements.cacheContainer.scrollTop;
            }
            this.#loadMore(element);
        });
    }

    #getTextContentElement(roomId) {
        if (!this.#cachedRooms[roomId]) {
            const textContent = document.createElement("div");
            textContent.className = "room-content scrollbar";
            this.#elements.cacheContainer.appendChild(textContent);
            if (roomId !== this.#room.id) {
                textContent.classList.add('hidden');
            }
            let obj = {};
            obj[roomId] = { content: textContent, scrollTop: null, firstMessageId: null };
            Object.assign(this.#cachedRooms, obj);

            textContent.addEventListener('scroll', () => {
                if (this.#isAtBottom(textContent)) {
                    this.#markAsRead(roomId);
                }
            });
        }

        return this.#cachedRooms[roomId]
    }

    async #eventHandler(event) {
        if (event.key === 'Enter') {
            if (event.shiftKey) {
                return;
            }
            event.preventDefault();
            await this.send();
            return;
        }

        if (event.key === 'Escape') {
            this.#elements.textInput.value = "";
            this.mode = TextController.MODE_SEND;
        }
    }

    #isScrollAtBottom(element) {
        return Math.abs(element.scrollHeight - element.scrollTop - element.clientHeight) < 1;
    }

    clearCache() {
        for (const [, room] of Object.entries(this.#cachedRooms)) {
            room.content.remove();
        }
        this.#cachedRooms = {}
    }

    /**
     * @param {string}  roomId
     * @param {boolean} reload
     */
    async load(roomId, reload = false) {
        for (const [, room] of Object.entries(this.#cachedRooms)) {
            room.content.classList.add('hidden');
        }

        // Room not loaded in cache yet
        if (!this.#cachedRooms[roomId] || reload) {
            /** @type {PageResult<MessageRepresentation>} */
            const result = await this.#core.load(roomId);

            if (result !== null) {
                const element = this.#getTextContentElement(roomId);

                const sortedResult = [...result.content].sort((a, b) => {
                    return new Date(a.createdDate) - new Date(b.createdDate);
                });

                element.content.innerHTML = "";
                for (const message of sortedResult) {
                    if (!element.firstMessageId) {
                        element.firstMessageId = message.id;
                    }
                    element.content.appendChild(this.create(message));
                }

                element.total = result.totalPages;
            }
        }

        const room = this.#cachedRooms[roomId];

        if (room.scrollTop == null) {
            room.scrollTop = this.#elements.cacheContainer.scrollHeight;
        }

        room.content.classList.remove('hidden');
        this.#elements.cacheContainer.scrollTop = room.scrollTop;

        if (this.#isAtBottom(room.content)) {
            this.#markAsRead(this.#room.id);
        }
    }

    async #loadMore(element) {
        if (element && element.scrollTop === 0) {
            let lastScrollHeight = this.#elements.cacheContainer.scrollHeight;

            /** @type {PageResult<MessageRepresentation>} */
            const result = await this.#core.loadMore(this.#room.id, element.firstMessageId);
            if (result !== null) {
                const invertedSortedResult = [...result.content].sort((a, b) => {
                    return new Date(a.createdDate) + new Date(b.createdDate);
                });

                for (const message of invertedSortedResult) {
                    element.content.prepend(this.create(message));
                    element.firstMessageId = message.id;
                }

                this.#elements.cacheContainer.scrollTop = this.#elements.cacheContainer.scrollHeight - lastScrollHeight;
            }
        }
    }

    /** @param {MessageNotification} data */
    message(data) {
        if (data.action === "ADD" && this.#user.id != data.message.user.id) {
            Alert.play('messageNew');
        }

        const message = data.message;
        const room = this.#cachedRooms[data.message.roomId];
        if (room) {
            switch (data.action) {
                case "ADD":
                    const isAtBottom = this.#isScrollAtBottom(this.#elements.cacheContainer);

                    // Add message
                    room.content.appendChild(this.create(message));

                    // Scroll auto
                    if (isAtBottom && this.#elements.cacheContainer.scrollTop < this.#elements.cacheContainer.scrollHeight) {
                        this.#elements.cacheContainer.scrollTop = this.#elements.cacheContainer.scrollHeight;
                    }
                    break;
                case "MODIFY":
                    document.getElementById(message.id).replaceWith(this.#createContent(message));
                    document.getElementById(`header-message-${message.id}`).replaceWith(this.#createHeader(message));
                    break;
                case "REMOVE":
                    document.getElementById(`container-${message.id}`).remove();
                    break;
                default:
                    console.error("Unsupported action : ", data.action);
                    break;
            }
        }
        if (data.action === "ADD" && this.#room.id !== data.message.roomId) {
            const mention = this.#hasMention(data.message) ? 1 : 0
            const roomToNotify = document.getElementById(`room-extension-dot-${data.message.roomId}`);
            if (roomToNotify) {
                roomToNotify.classList.remove('hidden');
                roomToNotify.setAttribute('mentions', '' + (roomToNotify.mentionsAttribute + mention))
            }
            const serverToNotify = document.getElementById(`server-notification-dot-${data.message.serverId}`);
            serverToNotify.classList.remove('hidden');
            serverToNotify.setAttribute('mentions', '' + (serverToNotify.mentionsAttribute + mention));
        }

        room.scrollTop = room.scrollHeight;
    }

    #addAttachment() {
        this.#elements.textAttachment.click();
        this.#elements.textAttachmentDiv.classList.remove('hidden');
        this.#elements.textInput.focus()
    }

    #removeAttachment() {
        this.#elements.textAttachment.value = "";
        this.#elements.textAttachmentDiv.classList.add('hidden');
        this.#elements.textInput.focus()
    }

    #pasteHandler(event) {
        const items = event.clipboardData?.items;
        if (!items) return;

        const dataTransfer = new DataTransfer();

        // Keep existing files
        for (const existingFile of this.#elements.textAttachment.files) {
            dataTransfer.items.add(existingFile);
        }

        let hasFile = false
        for (const item of items) {
            // Handle images or files
            if (item.kind === "file") {
                const file = item.getAsFile();
                if (file) {
                    if (file.name === "image.png") { // it's a screenshot, rename needed
                        const copy = new File([file], `screenshot-${Date.now()}.png`, {
                            type: file.type,
                            lastModified: file.lastModified
                        });
                        dataTransfer.items.add(copy);
                    } else {
                        dataTransfer.items.add(file);
                    }
                    hasFile = true;
                }
            }
        }

        this.#elements.textAttachment.files = dataTransfer.files;

        // Prevent default paste if files were detected
        if (hasFile) {
            this.#elements.textAttachmentDiv.classList.remove('hidden');
            event.preventDefault();
        }
    }

    async send() {
        let textInput = sanitizeString(this.#elements.textInput.value);

        if ((textInput == "" || textInput == null) && !this.#elements.textAttachment) {
            return;
        }

        const data = {
            text: textInput,
            answerTo: this.#repliedMessage?.id,
            medias: []
        }

        // Attachments
        const attachments = [];
        if (this.#elements.textAttachment && this.mode === TextController.MODE_SEND) {
            for (const element of this.#elements.textAttachment.files) {
                if (element.size < this.#attachmentMaxSize) {
                    data.medias.push({ name: element.name });
                    attachments[element.name] = element;
                } else {
                    await Modal.toggle({
                        icon: "error",
                        title: i18n.translateOne("attachement.error.size.title"),
                        html: i18n.translateOne("attachement.error.size.body", [element.name, humanFileSize(this.#attachmentMaxSize), humanFileSize(element.size)]),
                        showCancelButton: false,
                        focusConfirm: false,
                    });
                    return;
                }
            }
        }

        let result = await this.#sendMessage(data);
        if (result) {
            await this.#sendAttachements(result, attachments);

            // Clean file input
            this.#removeAttachment();
            this.#removeRepliedMessage();

            // Clean text input
            this.#elements.textInput.value = "";
            this.#elements.textInput.style.height = "auto";

            // Default mode
            this.mode = TextController.MODE_SEND;
            this.#editId = null;
            return;
        }

        await Modal.toggleError(
            i18n.translateOne("attachement.error.title"),
            i18n.translateOne("attachement.error.body")
        );
    }

    /**
     * @param data
     * @returns {Promise<MessageRepresentation|null>}
     */
    async #sendMessage(data) {
        let result = null;
        switch (this.mode) {
            case TextController.MODE_SEND:
                result = await this.#core.send(this.#room.id, data);
                break;

            case TextController.MODE_EDIT:
                result = await CoreServer.fetch(`/message/${editId}`, 'PATCH', data);
                break;

            default:
                console.error('Invalid mode');
                break;
        }
        return result;
    }

    async #sendAttachements(result, attachments) {
        if (this.mode === TextController.MODE_SEND) {
            for (const media of result.medias) {
                const formData = new FormData();
                formData.append("file", attachments[media.name]);
                await MediaServer.fetch(`/attachments/${media.id}`, 'POST', formData, false);
            }
        }
    }

    oninput(input) {
        input.style.height = "auto";
        input.style.height = input.scrollHeight + "px";
        if (input.value == "") {
            this.mode = TextController.MODE_SEND;
            this.#editId = null;
        }
    }

    /**
     * @param {MessageRepresentation} messageData
     * @param {boolean} urlPreview
     */
    create(messageData, {urlPreview = true} = {}) {
        const CONTAINER = document.createElement('div');
        CONTAINER.className = `message-container-message`;
        if (messageData.answeredTo) {
            CONTAINER.appendChild(this.#createAnswerHeader(messageData));
            CONTAINER.appendChild(this.#createHeader(messageData, false));
        } else {
            CONTAINER.appendChild(this.#createHeader(messageData, true));
        }
        CONTAINER.appendChild(this.#createContent(messageData, urlPreview));
        const MESSAGE = document.createElement('div');
        MESSAGE.id = `container-${messageData.id}`;
        MESSAGE.className = "message-container";
        if (RVC.userSettings().messageSetting === "default") {
            this.#addPicture(messageData, MESSAGE);
        }
        MESSAGE.appendChild(CONTAINER);
        return MESSAGE;
    }

    /**
     * Create a display for the answered message
     * @param {MessageRepresentation} messageData
     * @returns {HTMLElement}
     */
    #createAnswerHeader(messageData) {
        const answeredTo = messageData.answeredTo;
        const answerDiv = document.createElement('div');
        answerDiv.className = "message-answer-to";

        const icon = document.createElement('span');
        icon.innerHTML = '<revoice-icon-answer></revoice-icon-answer>';
        icon.style.marginRight = "4px";

        const label = document.createElement('span');
        label.dataset.i18n = 'message.answer.to';
        label.classList.add('no-wrap-text')
        label.textContent = 'Reply to';

        const messagePreview = document.createElement('div');
        messagePreview.className = "message-answer-preview";

        // Truncate text if too long
        messagePreview.textContent = answeredTo.text.length > 50
            ? answeredTo.text.substring(0, 50) + '...'
            : answeredTo.text;

        if (answeredTo.hasMedias) {
            const mediaIndicator = document.createElement('span');
            mediaIndicator.innerHTML = ' 📎';
            messagePreview.appendChild(mediaIndicator);
        }

        answerDiv.appendChild(icon);
        answerDiv.appendChild(label);
        answerDiv.appendChild(messagePreview);

        // Click handler to scroll to original message
        answerDiv.style.cursor = "pointer";
        answerDiv.onclick = () => {
            const originalMessage = document.getElementById(`container-${answeredTo.id}`);
            if (originalMessage) {
                originalMessage.scrollIntoView({ behavior: 'smooth', block: 'center' });
                originalMessage.style.backgroundColor = 'var(--highlight-color, rgba(59, 130, 246, 0.1))';
                setTimeout(() => {
                    originalMessage.style.backgroundColor = '';
                }, 2000);
            }
        };

        const answerHolder = document.createElement("div");
        answerHolder.appendChild(answerDiv)
        answerHolder.style.display = "flex";
        answerHolder.style.alignContent = "certer";
        const CONTEXT_MENU = this.#createContextMenu(messageData)
        if (CONTEXT_MENU) {
            answerHolder.appendChild(CONTEXT_MENU);
        }

        i18n.translatePage(answerHolder);
        renderEmojis(answerHolder);
        return answerHolder;
    }

    /**
     * @param {MessageRepresentation} messageData
     * @param {boolean} withButton
     */
    #createHeader(messageData, withButton = true) {
        const header = document.createElement('div');
        header.className = "message-header";
        header.id = `header-message-${messageData.id}`;
        const title = document.createElement('h3')
        title.className = "message-owner"
        title.innerHTML = `<span>${messageData.user.displayName}</span>
                           <span class="message-timestamp">${timestampToText(messageData.createdDate)}</span>
                           ${messageData.updatedDate ? '<span class="message-timestamp" data-i18n="message.edit">(edit)</span>' : ''}`;
        header.appendChild(title);
        if (withButton) {
            const CONTEXT_MENU = this.#createContextMenu(messageData)
            if (CONTEXT_MENU) {
                header.appendChild(CONTEXT_MENU);
            }
        }
        i18n.translatePage(header);
        return header
    }

    #addPicture(messageData, MESSAGE) {
        const picture = document.createElement('img');
        picture.src = MediaServer.profiles(messageData.user.id);
        picture.alt = "PFP"
        picture.className = "icon ring-2"
        picture.dataset.id = messageData.user.id
        MESSAGE.appendChild(picture);
    }

    /**
     * @param {MessageRepresentation} messageData
     * @param {boolean} urlPreview
     */
    #createContent(messageData, urlPreview = true) {
        const CONTENT = document.createElement('revoice-message');
        CONTENT.id = messageData.id;
        CONTENT.setAttribute("url-preview", urlPreview && messageData?.messageUrlPreview?.toString())
        CONTENT.innerHTML = `
            <script type="application/json" slot="medias">
                ${JSON.stringify(messageData.medias)}
            </script>
            <script type="text/markdown" slot="content">
                ${messageData.text}
            </script>
            <script type="application/json" slot="emotes">
                ${JSON.stringify(messageData.emotes)}
            </script>
            <script type="application/json" slot="reactions">
                ${JSON.stringify(messageData.reactions)}
            </script>
        `;
        return CONTENT;
    }

    /** @param {MessageRepresentation} repliedMessage */
    #reply(repliedMessage) {
        this.#repliedMessage = repliedMessage;
        this.#elements.textReplyMessage.dataset.messageId = repliedMessage.id;
        this.#elements.textInput.focus();
    }

    async #edit(id) {
        const result = await CoreServer.fetch(`/message/${id}`, 'GET');

        if (result) {
            this.mode = TextController.MODE_EDIT;
            this.#editId = id;
            this.#elements.textInput.value = result.text;
            this.#elements.textInput.style.height = "auto";
            this.#elements.textInput.style.height = this.#elements.textInput.scrollHeight + "px";
            this.#elements.textInput.focus();
        }
    }

    async #delete(id) {
        await CoreServer.fetch(`/message/${id}`, 'DELETE');
    }

    /**
     * @param {MessageRepresentation} messageData
     * @return {HTMLElement|null}
     */
    #createContextMenu(messageData) {

        const DIV = document.createElement('div');
        DIV.className = "message-context-menu";

        const ANSWER = document.createElement('div');
        ANSWER.className = "icon";
        ANSWER.innerHTML = "<revoice-icon-answer></revoice-icon-answer>";
        ANSWER.onclick = () => this.#reply(messageData);
        DIV.appendChild(ANSWER);

        const REACTIONS = document.createElement('div');
        REACTIONS.className = "icon";
        REACTIONS.innerHTML = "<revoice-icon-emoji></revoice-icon-emoji>";
        REACTIONS.addEventListener('click', (e) => {
            e.stopPropagation();
            emojiPicker.onEmojiSelect = (emoji) => {
                void CoreServer.fetch(`/message/${messageData.id}/reaction/${emoji.dataset.id}`, 'POST');
                emojiPicker.hide();
            };
            emojiPicker.show(e.clientX, e.clientY);
        });
        DIV.appendChild(REACTIONS);

        if (messageData.user.id === this.#user.id) {
            const EDIT = document.createElement('div');
            EDIT.className = "icon";
            EDIT.innerHTML = "<revoice-icon-pencil></revoice-icon-pencil>";
            EDIT.onclick = () => this.#edit(messageData.id);

            const DELETE = document.createElement('div');
            DELETE.className = "icon";
            DELETE.innerHTML = "<revoice-icon-trash></revoice-icon-trash>";
            DELETE.onclick = () => this.#delete(messageData.id);

            DIV.appendChild(EDIT);
            DIV.appendChild(DELETE);
        }

        return DIV
    }

    async getAttachmentMaxSize() {
        /** @type {MediaSettings} */
        const response = await MediaServer.fetch('/maxfilesize');
        if (response) {
            this.#attachmentMaxSize = response.maxFileSize;
        }
    }

    /**
     * Check if div is scrolled to bottom
     * @param {Element} div
     */
    #isAtBottom(div) {
        return div.scrollTop + div.clientHeight >= div.scrollHeight - 5;
    }

    #markAsRead(roomId) {
        void CoreServer.fetch(`/room/${roomId}/read-status`, 'PUT');
        const currentRoom = document.getElementById(`room-extension-dot-${roomId}`);
        const mentionsToRemove = currentRoom.mentionsAttribute
        currentRoom.classList.add('hidden');
        currentRoom.setAttribute('mentions', '0')

        const currentServer = document.getElementById(`server-notification-dot-${RVC.server.id}`);
        currentServer.setAttribute('mentions', '' + (currentServer.mentionsAttribute - mentionsToRemove))
        if (document.querySelectorAll("#rooms revoice-notification-dot:not(.hidden)").length === 0) {
            currentServer.classList.add('hidden');
        }
    }

    #hasMention(message) {
        return message?.answeredTo?.userId === this.#user.id;
    }
}