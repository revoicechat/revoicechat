import {apiFetch} from "../lib/tools.js";
import MediaServer from "../app/media/media.server.js";
import CoreServer from "../app/core/core.server.js";

class EmojiPicker {

    async init() {
        this.categories = await apiFetch("src/js/component/general.emoji.json").then(res => res.json());
        this.currentCategory = '01. custom_perso';
        this.onEmojiSelect = null;
    }

    addCustomEmojiCategory(name, category) {
        this.categories[name] = category
    }

    addCustomEmoji(category, emoji) {
        if (this.categories[category]) {
            this.categories[category].emojis.push(emoji);
        }
    }

    create() {
        const picker = document.createElement('div');
        picker.className = 'emoji-picker-content';
        picker.id = 'emoji-picker-content';
        picker.innerHTML = `
          <div class="emoji-picker-header">
            <div class="emoji-picker-categories">
              ${Object.keys(this.categories)
                .sort((a, b) => a.localeCompare(b))
                .map(key => `
                <button class="emoji-category-btn ${key === this.currentCategory ? 'active' : ''}"
                        data-category="${key}">
                  ${this.categories[key].icon}
                </button>
              `).join('')}
            </div>
          </div>
          <div class="emoji-picker-search">
            <input type="text" placeholder="Search..." class="emoji-search-input">
          </div>
          <div class="emoji-picker-body">
            <div class="emoji-grid"></div>
          </div>
        `;

        this.element = picker;
        this.renderEmojis();
        this.attachEvents();
        renderEmojis(picker);
        return picker;
    }

    renderEmojis(filter = '') {
        const grid = this.element.querySelector('.emoji-grid');
        let emojis
        if (filter) {
            emojis = Object.values(this.categories).flatMap(category => category.emojis);
            emojis = emojis.filter(e =>
                e.content === filter
                || e.names.some(name => name.toLowerCase().includes(filter.toLowerCase()))
                || e.description.toLowerCase().includes(filter.toLowerCase())
            );
        } else {
            const category = this.categories[this.currentCategory];
            emojis = category.emojis;
        }

        if (emojis.length === 0) {
            grid.innerHTML = '<div class="emoji-empty">No emojis found</div>';
            return;
        }

        grid.innerHTML = emojis.map(emoji => {
            const data = emoji.data || emoji.content;
            const emojiId = emoji.id || emoji.content;
            return `<button class="emoji-item" data-id="${emojiId}" data-emoji="${data}">${emoji.content}</button>`
        }).join('');

        for (const item of grid.querySelectorAll('.emoji-item')) {
            item.onclick = () => this.onEmojiSelect(item);
        }
        renderEmojis(grid);
    }

    attachEvents() {
        for (const btn of this.element.querySelectorAll('.emoji-category-btn')) {
            btn.addEventListener('click', () => {
                this.currentCategory = btn.dataset.category;
                for (const b of this.element.querySelectorAll('.emoji-category-btn')) {
                    b.classList.remove('active');
                }
                btn.classList.add('active');

                this.renderEmojis();
            });
        }

        const searchInput = this.element.querySelector('.emoji-search-input');
        searchInput.addEventListener('input', (e) => {
            this.renderEmojis(e.target.value);
        });
    }

    show(x = 0, y = 0) {
        const pickerContainer = document.getElementById('emoji-picker');
        pickerContainer.classList.toggle('show');
        const w = pickerContainer.offsetWidth;
        const h = pickerContainer.offsetHeight;
        const vw = innerWidth;
        const vh = innerHeight;
        const left = Math.min(x, vw - w);
        const top = Math.min(y - 10, vh - h);
        pickerContainer.style.left = (left - 100) + "px";
        pickerContainer.style.top = (top - 50) + "px";
    }

    hide() {
        const pickerContainer = document.getElementById('emoji-picker');
        this.onEmojiSelect = null;
        pickerContainer.classList.remove('show');
    }
}

async function initCustomGeneral(picker) {
    /** @type {EmoteRepresentation[]} */
    const emojis = await CoreServer.fetch(`/emote/global`);
    initCustomEmojiCategory(picker,
        '03. custom_general',
        '<img src="src/img/favicon.png" alt="revoice"/>',
        Array.from(emojis).map(emoji => {
            return {
                link: emoji.id,
                content: emoji.name,
                description: emoji.name,
                names: emoji.keywords
            }
        })
    )
}

async function initCustomServer(picker) {
    if (RVC.server.id) {
        /** @type {EmoteRepresentation[]} */
        const emojis = await CoreServer.fetch(`/emote/server/${RVC.server.id}`);
        initCustomEmojiCategory(picker, '02. custom_server',
            '🏠',
            Array.from(emojis).map(emoji => {
                return {
                    link: emoji.id,
                    content: emoji.name,
                    description: emoji.name,
                    names: emoji.keywords
                }
            })
        )
    }
}

async function initCustomUser(picker) {
    /** @type {EmoteRepresentation[]} */
    const emojis = await CoreServer.fetch(`/emote/me`);
    initCustomEmojiCategory(picker, '01. custom_perso',
        `<img class="emoji ${RVC.user.id}"
                   src="${MediaServer.profiles(RVC.user.id)}"
                   data-id="${RVC.user.id}"
                   style="border-radius: 9999px;"
                   alt="user-emote"/>`,
        Array.from(emojis).map(emoji => {
            return {
                link: emoji.id,
                content: emoji.name,
                description: emoji.name,
                names: emoji.keywords
            }
        })
    )
}

function initCustomEmojiCategory(picker, name, icon, emojis) {
    const emojiCategory = {
        icon: icon,
        emojis: []
    }
    for (const emote of emojis) {
        emojiCategory.emojis.push({
            content: `<img class="emoji" src="${MediaServer.emote(emote.link)}" alt="${emote.content}" title=":${emote.content}:"/>`,
            description: emote.description,
            names: emote.names,
            id: emote.link,
            data: `:${emote.content}:`
        })
    }
    picker.addCustomEmojiCategory(name, emojiCategory)
}

/** @param {HTMLElement|ShadowRoot} element */
function renderEmojis(element) {
    twemoji.parse(element, {
        folder: 'assets',
        ext: '.svg',
        base: MediaServer.emoteAssets(),
        className: "emoji"
    })
}

export { EmojiPicker, initCustomGeneral, initCustomServer, initCustomUser, renderEmojis }