import { i18n } from "../lib/i18n.js";
import MediaServer from "../app/media/media.server.js";
import CoreServer from "../app/core/core.server.js";
import Modal from "./modal.component.js";

class EmojiManager extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this.emojis = [];
        this.currentEditId = null;
    }

    connectedCallback() {
        this.path = this.getAttribute("path")
        this.render();
        this.setupEventListeners();
        this.loadEmojisFromSlot();
        this.setupSlotObserver();
    }

    setupSlotObserver() {
        // Observer to detect slot changes
        const slot = this.shadowRoot.querySelector('slot');

        slot.addEventListener('slotchange', () => {
            this.loadEmojisFromSlot();
        });

        // MutationObserver to detect changes in slot content
        const observer = new MutationObserver(() => {
            this.loadEmojisFromSlot();
        });

        observer.observe(this, {
            childList: true,
            subtree: true,
            characterData: true
        });
    }

    loadEmojisFromSlot() {
        const slotContent = this.querySelector('[slot="emojis-data"]');
        if (slotContent) {
            try {
                const data = JSON.parse(slotContent.textContent.trim());
                if (Array.isArray(data)) {
                    this.emojis = data;
                    this.updateEmojiList();
                }
            } catch (e) {
                console.error('Error parsing emojis JSON:', e);
            }
        }
    }

    setupEventListeners() {
        const addForm = this.shadowRoot.getElementById('addForm');
        const addFileInput = this.shadowRoot.getElementById('emojiFile');
        addForm.addEventListener('submit', (e) => {
            e.preventDefault();
            this.addEmoji();
        });
        // Preview for add form
        addFileInput.addEventListener('change', (e) => {
            this.handleFilePreview(e.target.files[0], this.shadowRoot.getElementById('addPreview'));
        });
    }

    handleFilePreview(file, preview) {
        if (!file) return;

        const reader = new FileReader();
        reader.onload = (e) => {
            const src = e.target.result
            if (typeof src === 'string') {
                preview.innerHTML = `<img src="${src}" alt="Preview">`;
            } else {
                console.warn('Unexpected non-string FileReader result', src);
            }
            preview.style.display = 'flex';
        };
        reader.readAsDataURL(file);
    }

    validateName(name) {
        if (!name.trim()) {
            return "emote.new.error.name.required";
        }
        if (/\s/.test(name)) {
            return "emote.new.error.name.no.space";
        }
        return null;
    }

    async addEmoji() {
        const fileInput = this.shadowRoot.getElementById('emojiFile');
        const nameInput = this.shadowRoot.getElementById('emojiName');
        const keywordsInput = this.shadowRoot.getElementById('emojiKeywords');

        // Reset errors
        this.shadowRoot.getElementById('fileError').textContent = '';
        this.shadowRoot.getElementById('nameError').textContent = '';

        // Validate
        const validate = this.validateName(nameInput.value);
        if (validate) {
            const nameError = i18n.translateOne(validate);
            if (nameError) {
                this.shadowRoot.getElementById('nameError').textContent = nameError;
                return;
            }
        }

        if (!fileInput.files[0]) {
            this.shadowRoot.getElementById('fileError').textContent = i18n.translateOne("emote.new.error.picture");
            return;
        }

        // Check if name already exists
        if (this.emojis.some(e => e.name === nameInput.value.trim())) {
            this.shadowRoot.getElementById('nameError').textContent = i18n.translateOne("emote.new.error.duplicate");
            return;
        }

        // Read file
        const file = fileInput.files[0];
        const keywords = keywordsInput.value
            .split(',')
            .map(k => k.trim())
            .filter(k => k.length > 0);

        try {
            /** @type {EmoteRepresentation} */
            const emojiData = await CoreServer.fetch(`/emote/${this.path}`, 'PUT', {
                fileName: file.name,
                content: nameInput.value.trim(),
                keywords: keywords
            }
            );
            const formData = new FormData();
            formData.append('file', file);

            await MediaServer.fetch(`/emote/${emojiData.id}`, 'POST', formData);

            // Temporary: Store image locally until API is integrated
            const reader = new FileReader();
            reader.onload = (e) => {
                this.emojis.push(emojiData);

                // Dispatch custom event
                this.dispatchEvent(new CustomEvent('emoji-added', {
                    detail: { emoji: emojiData },
                    bubbles: true,
                    composed: true
                }));

                // Reset form
                fileInput.value = '';
                nameInput.value = '';
                keywordsInput.value = '';
                this.shadowRoot.getElementById('addPreview').style.display = 'none';
                this.shadowRoot.getElementById('addPreview').innerHTML = '';

                this.updateEmojiList();
            };
            reader.readAsDataURL(file);

        } catch (error) {
            console.error('Error adding emoji:', error);
            this.shadowRoot.getElementById('fileError').textContent = i18n.translateOne("emote.new.error");
        }
    }

    async deleteEmoji(id) {
        Modal.toggle({
            title: i18n.translateOne("emote.delete.popup", [id]),
            showCancelButton: true,
            focusCancel: true,
            confirmButtonText: i18n.translateOne("modal.delete"),
            confirmButtonClass: "danger",
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    await CoreServer.fetch(`/emote/${id}`, 'DELETE');

                    // Temporary: Delete locally until API is integrated
                    const emoji = this.emojis.find((e) => e.id === id);
                    this.emojis = this.emojis.filter(e => e.id !== id);

                    // Dispatch custom event
                    this.dispatchEvent(new CustomEvent('emoji-deleted', {
                        detail: { emoji },
                        bubbles: true,
                        composed: true
                    }));

                    this.updateEmojiList();
                } catch (error) {
                    console.error('Error deleting emoji:', error);
                    alert('Failed to delete emoji');
                }
            }
        });
    }

    openEditModal(id) {
        const emoji = this.emojis.find(e => e.id === id);
        if (!emoji) return;
        Modal.toggle({
            title: 'Edit emoji',
            showCancelButton: true,
            focusConfirm: false,
            confirmButtonText: i18n.translateOne("emote.new.button"),
            html: `
                <form id="editForm">
                    <div id="update-emote-popup" class="form-row-with-preview">
                        <div style="flex: 1; display: flex; flex-direction: column; gap: 1rem;">
                            <div class="emotes-form-group">
                                <label for="editName" data-i18n="emote.new.name">Name (no spaces)</label>
                                <input type="text" id="editName" required>
                                <div class="error" id="editNameError"></div>
                            </div>
                            <div class="emotes-form-group">
                                <label for="editKeywords" data-i18n="emote.new.keyword">Keywords (comma-separated)</label>
                                <input type="text" id="editKeywords">
                            </div>
                            <div class="emotes-form-group">
                                <label for="editEmojiFile" data-i18n="emote.new.picture">Change image</label>
                                <input type="file" id="editEmojiFile" accept="image/*">
                            </div>
                        </div>
                        <div class="preview-container" id="editPreview"></div>
                    </div>
                </form>`,
            didOpen: () => {
                this.currentEditId = id;
                document.querySelector('#editName').value = emoji.name;
                document.querySelector('#editKeywords').value = emoji.keywords.join(', ');
                document.querySelector('#editNameError').textContent = '';
                const editEmojiFile = document.querySelector('#editEmojiFile')
                editEmojiFile.value = '';

                const editPreview = document.querySelector('#editPreview');
                editPreview.innerHTML = `<img src="${MediaServer.emote(id)}" alt="${emoji.name}">`;
                editPreview.style.display = 'flex';

                editEmojiFile.addEventListener('change', (e) => {
                    this.handleFilePreview(e.target.files[0], editPreview);
                });
                i18n.translatePage(document.getElementById("update-emote-popup"))
            },
            preConfirm: () => {
                const name = document.querySelector('#editName').value;
                const keywords = document.querySelector('#editKeywords').value.split(",");
                const fileInput = document.querySelector('#editEmojiFile');

                const validate = this.validateName(name);
                if (validate) {
                    const nameError = i18n.translateOne(validate);
                    if (nameError) {
                        document.querySelector('#editNameError').textContent = nameError;
                        return;
                    }
                    if (this.emojis.some(e => e.name === name.trim() && e.id !== this.currentEditId)) {
                        document.querySelector('#editNameError').textContent = i18n.translateOne("emote.new.error.duplicate");
                        return;
                    }
                }
                return { name: name, keywords: keywords, file: fileInput.files[0] };
            }
        }).then(async (result) => {
            if (result.isConfirmed) {
                this.saveEdit(result.data);
            }
        });
    }

    closeModal() {
        this.shadowRoot.getElementById('editModal').classList.remove('active');
        this.currentEditId = null;
    }

    saveEdit(result) {
        const emoji = this.emojis.find(e => e.id === this.currentEditId);
        if (emoji) {
            const oldName = emoji.name;
            const keywords = result.keywords.map(k => k.trim()).filter(k => k.length > 0);

            const updateEmoji = async () => {
                try {
                    await CoreServer.fetch(`/emote/${this.currentEditId}`, 'PATCH', {
                        fileName: result.file?.name,
                        content: result.name.trim(),
                        keywords: keywords
                    });

                    if (result.file) {
                        const formData = new FormData();
                        formData.append('file', result.file);
                        await MediaServer.fetch(`/emote/${this.currentEditId}`, "POST", formData);
                    } else {
                        // Dispatch custom event
                        this.dispatchEvent(new CustomEvent('emoji-updated', {
                            detail: { emoji, oldName },
                            bubbles: true,
                            composed: true
                        }));

                        this.closeModal();
                        this.updateEmojiList();
                    }
                } catch (error) {
                    console.error('Error updating emoji:', error);
                }
            };
            updateEmoji();
        }
    }

    updateEmojiList() {
        const grid = this.shadowRoot.getElementById('emojiGrid');

        if (this.emojis.length === 0) {
            grid.innerHTML = `
                <div class="empty-state">
                    <revoice-icon-emoji></revoice-icon-emoji>
                    <h3 data-i18n="emote.empty.title">No emojis yet</h3>
                    <p  data-i18n="emote.empty.body" >Add one above!</p>
                </div>
            `;
            return;
        }

        grid.innerHTML = this.emojis.map(emoji => `
            <div class="config-item">
                <div class="emoji-header">
                    <div class="emoji-preview">
                        <img src="${MediaServer.emote(emoji.id)}" alt="${emoji.name}">
                    </div>
                    <div class="emoji-info">
                        <div class="emoji-name">:${emoji.name}:</div>
                        <div class="emoji-keywords">${emoji.keywords.length > 0 ? emoji.keywords.join(', ') : '—'}</div>
                    </div>
                </div>
                <div class="emoji-actions">
                    <button class="emote-list-button btn-secondary btn-small" data-action="edit" data-id="${emoji.id}">
                        <revoice-icon-pencil></revoice-icon-pencil> <span data-i18n="emote.item.edit">Edit</span>
                    </button>
                    <button class="emote-list-button btn-danger btn-small" data-action="delete" data-id="${emoji.id}">
                        <revoice-icon-trash></revoice-icon-trash> <span data-i18n="emote.item.delete">Delete</span>
                    </button>
                </div>
            </div>
        `).join('');

        // Add event listeners for buttons
        for (const btn of grid.querySelectorAll('button[data-action="edit"]')) {
            btn.addEventListener('click', () => this.openEditModal(btn.dataset.id));
        }

        for (const btn of grid.querySelectorAll('button[data-action="delete"]')) {
            btn.addEventListener('click', () => this.deleteEmoji(btn.dataset.id));
        }
        i18n.translatePage(grid)
    }

    render() {
        this.shadowRoot.innerHTML = `
            <link href="src/css/main.css" rel="stylesheet" />
            <link href="src/css/emoji.css" rel="stylesheet" />
            <link href="src/js/component/emoji.manager.component.css" rel="stylesheet" />
            <div class="container">
                <slot name="emojis-data"></slot>

                <div class="add-section">
                    <form class="add-form" id="addForm">
                        <div class="form-row-with-preview">
                            <div style="flex: 1; display: flex; flex-direction: column; gap: 1rem;">
                                <div class="form-row">
                                    <div class="emotes-form-group">
                                        <label for="emojiFile" data-i18n="emote.new.picture">Image</label>
                                        <input type="file" id="emojiFile" accept="image/*" required>
                                        <div class="error" id="fileError"></div>
                                    </div>
                                    <div class="emotes-form-group">
                                        <label for="emojiName" data-i18n="emote.new.name">Name (no spaces)</label>
                                        <input type="text" id="emojiName" data-i18n-placeholder="emote.new.name.placeholder" placeholder="e.g. super_emoji" required>
                                        <div class="error" id="nameError"></div>
                                    </div>
                                </div>
                                <div class="emotes-form-group">
                                    <label for="emojiKeywords" data-i18n="emote.new.keyword">Keywords (optional, comma-separated)</label>
                                    <input type="text" id="emojiKeywords" data-i18n-placeholder="emote.new.keyword.placeholder" placeholder="e.g. happy, smile, joy">
                                </div>
                            </div>
                            <div class="preview-container" id="addPreview"></div>
                        </div>
                        <button type="submit" class="btn-primary" data-i18n="emote.new.button">Add emote</button>
                    </form>
                </div>

                <div class="emoji-list">
                    <div class="emoji-grid" id="emojiGrid"></div>
                </div>
            </div>
        `;
    }
}

customElements.define('revoice-emoji-manager', EmojiManager);