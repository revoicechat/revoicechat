import {containsOnlyEmotes, countEmotes} from "../lib/emote.utils.js";
import MediaServer from "../app/media/media.server.js";
import CoreServer from "../app/core/core.server.js";
import {isUUID} from "../lib/string.utils.js";
import {renderEmojis} from "./emoji.component.js";
import Modal from "./modal.component.js";
import {i18n} from "../lib/i18n.js";
import {statusToColor} from "../lib/tools.js";

class MessageComponent extends HTMLElement {
    /** @type string */
    markdown
    /** @type EmoteRepresentation[] */
    emotes
    /** @type MessageReaction[] */
    reactions

    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.markdown = '';
        this.emotes = []
        this.reactions = []
    }

    static get observedAttributes() {
        return ['markdown', 'theme', 'data-theme'];
    }

    /** generate the data in slot */
    connectedCallback() {
        this.#setupShadowDOM();
        this.#render();
        this.#updateTheme()
    }

    /** update the data in slot */
    attributeChangedCallback(name, oldValue, newValue) {
        if (name === 'markdown' && oldValue !== newValue) {
            this.markdown = newValue || '';
            this.#render();
        } else if ((name === 'theme' || name === 'data-theme') && oldValue !== newValue) {
            this.#updateTheme();
        }
    }

    #setupShadowDOM() {
        // Create the shadow DOM structure
        this.shadowRoot.innerHTML = `
                    <link href="src/js/component/message.component.css" rel="stylesheet" />
                    <div class="container">
                        <div class="markdown-content" id="content"></div>
                        <slot name="medias" style="display: none;"></slot>
                        <slot name="content" style="display: none;"></slot>
                        <slot name="emotes" style="display: none;"></slot>
                        <slot name="reactions" style="display: none;"></slot>
                    </div>
                `;

        // Listen for slotchange events
        this.shadowRoot.addEventListener('slotchange', (e) => {
            if (e.target.name === 'medias') {
                this.#handleSlottedMedias();
            }
            if (e.target.name === 'content') {
                this.#handleSlottedContent();
            }
            if (e.target.name === 'emotes') {
                this.#handleSlottedEmotes();
            }
            if (e.target.name === 'reactions') {
                this.#handleSlottedReaction();
            }
        });
    }

    #handleSlottedMedias() {
        const mediasSlot = this.shadowRoot.querySelector('slot[name="medias"]');
        const slottedElements = mediasSlot.assignedElements();
        for (const element of slottedElements) {
            if (element.tagName === 'SCRIPT' && element.type === 'application/json') {
                this.medias = JSON.parse(element.textContent)
                break;
            }
        }
    }

    #handleSlottedContent() {
        const contentSlot = this.shadowRoot.querySelector('slot[name="content"]');
        const slottedElements = contentSlot.assignedElements();

        for (const element of slottedElements) {
            if (element.tagName === 'SCRIPT' && element.type === 'text/markdown') {
                this.markdown = element.textContent.trim();

                if (this.markdown) {
                    this.#render();
                }

                break;
            }
        }
    }

    #handleSlottedEmotes() {
        const emotesSlot = this.shadowRoot.querySelector('slot[name="emotes"]');
        const slottedElements = emotesSlot.assignedElements();
        for (const element of slottedElements) {
            if (element.tagName === 'SCRIPT' && element.type === 'application/json') {
                this.emotes = JSON.parse(element.textContent)
                break;
            }
        }
    }

    #handleSlottedReaction() {
        const reactionsSlot = this.shadowRoot.querySelector('slot[name="reactions"]');
        const slottedElements = reactionsSlot.assignedElements();
        for (const element of slottedElements) {
            if (element.tagName === 'SCRIPT' && element.type === 'application/json') {
                this.reactions = JSON.parse(element.textContent)
                break;
            }
        }
    }

    #hideSlots() {
        this.shadowRoot.querySelector('.container').className = 'container';
    }

    #updateTheme() {
        let theme = getComputedStyle(this).getPropertyValue("--hljs-theme").trim();
        theme = theme.substring(1, theme.length - 1)
        const link = document.createElement("link");
        link.id = "highlightjs-theme";
        link.rel = "stylesheet";
        link.href = theme;
        this.shadowRoot.appendChild(link);
    }

    #render() {
        const contentDiv = this.shadowRoot.getElementById('content');

        // Check if there's slotted content
        if (!this.markdown) {
            this.#handleSlottedContent();
        }
        this.#handleSlottedMedias();
        this.#handleSlottedEmotes();
        this.#handleSlottedReaction();

        if (typeof marked === 'undefined') {
            contentDiv.innerHTML = '<p style="color: #ff6b6b;">marked.js library not loaded</p>';
            return;
        }
        try {
            this.#setupMarked()
            this.#hideSlots();

            contentDiv.innerHTML = this.#injectMedias();

            if (this.markdown) {
                if (containsOnlyEmotes(this.markdown, this.#emotesNames())) {
                    contentDiv.innerHTML = this.#injectEmojis(this.#removeTags(this.markdown))
                    if (countEmotes(this.markdown, this.#emotesNames()) === 1) {
                        contentDiv.classList.add('stickers-emoji')
                    } else {
                        contentDiv.classList.add('only-emoji')
                    }
                } else {
                    contentDiv.innerHTML += this.#injectEmojis(marked.parse(this.#removeTags(this.markdown)));
                }
            }

            this.#renderCodeTemplate(contentDiv);

        } catch (error) {
            console.error('Markdown parsing error:', error);
            contentDiv.innerHTML = `<p style="color: #ff6b6b;">Error parsing markdown: ${error.message}</p>`;
        }
        this.#renderOpenGraph(contentDiv)
        this.#renderReactions(contentDiv)
        renderEmojis(this.shadowRoot);
    }

    #renderCodeTemplate(contentDiv) {
        for (const block of contentDiv.querySelectorAll('pre code')) {
            hljs.highlightElement(block);
        }
    }

    /** Identify HTML tags in the input string. Replacing the identified HTML tag with a null string.*/
    #removeTags(str) {
        if (!str) return "";
        const div = document.createElement("div");
        div.innerHTML = String(str);
        return div.textContent || "";
    }

    #injectEmojis(inputText) {
        return inputText.replaceAll(/:([A-Za-z0-9\-_]+):/g, (_, emoji) => {
            if (this.emotes) {
                const emote = Array.from(this.emotes).find(item => item.name === emoji);
                if (emote) {
                    return `<img class="emoji" src="${MediaServer.emote(emote.id)}" alt="${emoji}" title=":${emoji}:">`;
                }
            }
            return `:${emoji}:`
        });
    }

    #setupMarked() {
        const renderer = new marked.Renderer();
        renderer.heading = function ({tokens: e, depth: t}) {
            const text = this.parser.parse(e);
            const DIV = document.createElement('div');
            DIV.innerHTML = text
            const p = DIV.children.item(0)
            p.innerHTML = '#'.repeat(t) + " " + p.innerHTML;
            return p.innerHTML;
        }
        renderer.link = function ({href: e, title: t, tokens: n}) {
            // Allow only http(s), www, or IP-style links
            if (/^(https?:\/\/|www\.|(\d{1,3}\.){3}\d{1,3})/.test(e)) {
                return `<a href="${e}" target="_blank" rel="noopener noreferrer">${e}</a>`;
            }
            return this.parser.parse(n);
        }

        marked.use({renderer})
        marked.use({
            breaks: true,
            gfm: true
        });
    }

    #injectMedias() {
        let result = "";
        if (this.medias) {
            for (const media of this.medias) {
                if (media.status === "STORED") {
                    result += `<revoice-attachement-message id="${media.id}" name="${media.name}" type="${media.type}"></revoice-attachement-message>`
                }
            }
        }
        return result;
    }

    /** @return {string[]} */
    #emotesNames() {
        return Array.from(this.emotes).map(item => item.name)
    }

    #renderOpenGraph(contentDiv) {
        if (this.getAttribute("url-preview") === "false") {
            return
        }
        const openGraphCard = document.createElement("div")
        contentDiv.appendChild(openGraphCard)
        const id = this.getAttribute("id")
        CoreServer.fetch(`/message/${id}/open-graph`)
                .then(res => {
                    if (res) {
                        const card = document.createElement("revoice-opengraph-card")
                        card.ogdata = res
                        openGraphCard.appendChild(card)
                    }
                })
    }

    #renderReactions(contentDiv) {
        if (this.reactions.length === 0) {
            return
        }
        const REACTIONS = document.createElement("div")
        REACTIONS.className = "message-reactions"
        const printReaction = (element, emoji, number) => {
            if (number <= 0) {
                element.remove()
            } else {
                element.appendChild(isUUID(emoji) ? this.#emoji(emoji) : this.#span(emoji))
                element.appendChild(this.#span(number))
            }

        }
        for (const reaction of this.reactions) {
            const self = reaction.users.includes(RVC.user.id);
            const emoji = document.createElement("div");
            printReaction(emoji, reaction.emoji, reaction.users.length)
            emoji.className = `message-reaction ${self ? 'self' : ''}`
            emoji.onclick = () => {
                void CoreServer.fetch(`/message/${this.id}/reaction/${reaction.emoji}`, 'POST');
                printReaction(emoji, reaction.emoji, reaction.users.length + (self ? -1 : 1))
            }
            emoji.addEventListener('contextmenu', async (e) => {
                e.preventDefault();
                console.log('Right clicked!');
                const users = await CoreServer.fetch(`/server/${RVC.server.id}/user`, 'GET');
                await Modal.toggle({
                    html: `
                        <div class='popup'>
                            ${reaction.users
                                      .map(u => users.find(usr => usr.id === u))
                                      .map(u => this.#createUser(u))
                                      .join("")}
                        </div>`,
                    didOpen: () => {
                        const titre = document.querySelector('.dialog-title');
                        titre.appendChild(isUUID(reaction.emoji) ? this.#emoji(reaction.emoji) : this.#span(reaction.emoji))
                    }
                })
            });
            REACTIONS.appendChild(emoji)
        }
        contentDiv.appendChild(REACTIONS)
    }

    #createUser(user) {
        return `<div class="${user.id} user-profile">
                    <div class="relative">
                        <img src="${MediaServer.profiles(user.id)}" alt="PFP"
                             style="border-radius: 9999px;width: 2rem; height: 2rem; aspect-ratio: auto;"
                             class="icon ring-2"
                             data-id="${user.id}"
                             name="user-picture-${user.id}" />
                    </div>
                    <div class="user">
                        <h2 class="name" title="${user.displayName}" name="user-name-${user.id}">${user.displayName}</h2>
                    </div>
                </div>`;
    }

    /** @param {string} data */
    #span(data) {
        const span = document.createElement("span")
        span.style.display = "flex"
        span.style.alignItems = "center"
        span.innerText = data
        return span;
    }

    /** @param {string} emoji */
    #emoji(emoji) {
        const img = document.createElement("img")
        img.src = MediaServer.emote(emoji)
        img.className = 'emoji'
        img.alt = emoji
        return img;
    }
}

customElements.define('revoice-message', MessageComponent);