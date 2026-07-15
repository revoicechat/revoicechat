import {renderEmojis} from "./emoji.component.js";
import {getCustomEmoji} from "../app/emoji.js";

const codeBlock = /^```(\w+)?\n([\s\S]*?)^```$/gm
const code      = /`([^`]+)`/g;
const bold      = /\*\*(.+?)\*\*/g;
const italic    = /(?<!\*)\*(?!\*)(.+?)(?<!\*)\*(?!\*)/g;
const emote     = /:(\w+):/g;
const userAt    = /<@userId:([0-9a-fA-F-]{36})>/g;
const roleAt    = /<@&roleId:([0-9a-fA-F-]{36})>/g;

const messagePlaceholder = "Send a message";

const textPatterns = [
    {type: 'codeBlock', regexp: codeBlock, groups: ['lang', 'content'] },
    {type: 'code',      regexp: code,      groups: ['content'] },
    {type: 'bold',      regexp: bold,      groups: ['content'] },
    {type: 'italic',    regexp: italic,    groups: ['content'] },
    {type: 'emote',     regexp: emote,     groups: ['content'] },
    {type: 'userAt',    regexp: userAt,    groups: ['content'] },
    {type: 'roleAt',    regexp: roleAt,    groups: ['content'] },
]

class TextareaComponent extends HTMLElement {
    #cursorPos = null;

    /** @type HTMLPreElement */
    textPreview;
    /** @type HTMLTextAreaElement */
    textInput;

    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.#initStyle();
        this.#initTextInput();
        this.#initTextPreview();
        this.shadowRoot.appendChild(this.textInput);
        this.shadowRoot.appendChild(this.textPreview);

        this.textInput.addEventListener('input', () => this.#syncMessage());
        this.textInput.addEventListener('paste', () => this.#syncMessage());
        this.textInput.addEventListener('keyup',  () => this.#syncMessage());
        this.textInput.addEventListener('mouseup',() => this.#syncMessage());
        this.textInput.addEventListener('focus',   () => this.#syncMessage());
        this.textInput.addEventListener('blur',   () => {
            this.#cursorPos = null;
            this.#removeCursor();
        });
        this.textPreview.addEventListener('click', (e) => this.#handlePreviewClick(e));
    }

    get value() {
        return this.textInput.value;
    }

    set value(value) {
        this.textInput.value = value;
        this.#syncMessage();
    }

    #initStyle() {
        const componentCssLink = document.createElement("link");
        componentCssLink.href = "src/js/component/textarea.component.css";
        componentCssLink.rel = "stylesheet";
        this.shadowRoot.appendChild(componentCssLink);
        const theme = document.createElement("link");
        theme.href = "src/css/highlightjs/dark.css";
        theme.rel = "stylesheet";
        this.shadowRoot.appendChild(theme);
    }

    #initTextPreview() {
        this.textPreview = document.createElement('pre');
        this.textPreview.id = 'text-preview';
        this.#updatePlaceholder();
    }

    #initTextInput() {
        this.textInput = document.createElement('textarea');
        this.textInput.style.opacity = '1';
        this.textInput.style.position = 'absolute';
        this.textInput.style.top = '-120px';
        this.textInput.id = 'text-input';
        this.textInput.rows = 1;
    }

    #syncMessage() {
        this.#cursorPos = this.textInput.selectionStart;
        this.#render();
        this.#updatePlaceholder(true)
    }

    #render() {
        this.textPreview.innerHTML = '';
        let raw = this.textInput.value;
        if (this.#cursorPos === null || !this.textInput.matches(':focus')) {
            const parts = this.#splitMarkdown(raw);
            for (const part of parts) {
                this.textPreview.appendChild(this.#renderPart(part));
            }
            return;
        }
        const before = raw.slice(0, this.#cursorPos);
        const after  = raw.slice(this.#cursorPos);

        for (const part of this.#splitMarkdown(before)) {
            this.textPreview.appendChild(this.#renderPart(part));
        }

        const caret = document.createElement('span');
        caret.className = 'cursor-caret';
        this.textPreview.appendChild(caret);

        for (const part of this.#splitMarkdown(after)) {
            this.textPreview.appendChild(this.#renderPart(part));
        }
    }

    #renderPart(part) {
        let el;
        switch (part.type) {
            case 'text':      el = this.#textPart(part.content); break;
            case 'codeBlock': el = this.#codeBlockPart(part.content, part.lang); break;
            case 'code':      el = this.#codePart(part.content); break;
            case 'bold':      el = this.#boldPart(part.content); break;
            case 'italic':    el = this.#italicPart(part.content); break;
            case 'emote':     el = this.#emotePart(part.content); break;
            case 'userAt':    el = this.#userAtPart(part.content); break;
            case 'roleAt':    el = this.#roleAtPart(part.content); break;
        }
        el._rawLength = part._rawLength;
        return el;
    }

    #updatePlaceholder(withCaret = false) {
        this.textPreview.classList.remove('placeholder')
        if (!this.textInput.value) {
            if (withCaret) {
                this.textPreview.innerHTML = `<span class="cursor-caret"></span>${messagePlaceholder}`
            } else {
                this.textPreview.innerHTML = messagePlaceholder;
            }
            this.textPreview.classList.add('placeholder')
        }
    }

    /** @param {string} markdown */
    #splitMarkdown(markdown) {
        const combined = new RegExp(textPatterns.map(p => `(${p.regexp.source})`).join('|'), 'gm');
        const result = [];
        let lastIndex = 0;
        for (const match of markdown.matchAll(combined)) {
            if (match.index > lastIndex) {
                const content = markdown.slice(lastIndex, match.index);
                result.push({ type: 'text', content, _rawLength: content.length });
            }
            let groupOffset = 1;
            for (const pattern of textPatterns) {
                const groupCount = pattern.groups.length;
                const fullMatch = match[groupOffset];
                if (fullMatch !== undefined) {
                    const node = { type: pattern.type, _rawLength: match[0].length };
                    pattern.groups.forEach((name, i) => {
                        node[name] = match[groupOffset + 1 + i] ?? null;
                    });
                    result.push(node);
                    break;
                }
                groupOffset += 1 + groupCount;
            }
            lastIndex = match.index + match[0].length;
        }
        if (lastIndex < markdown.length) {
            const content = markdown.slice(lastIndex);
            result.push({ type: 'text', content, _rawLength: content.length });
        }
        return result;
    }

    #textPart(content) {
        const text = document.createElement('span')
        text.innerText = content;
        renderEmojis(text)
        return text;
    }

    #codeBlockPart(content, lang) {
        const div = document.createElement('div')
        const header = document.createElement('span')
        header.innerText = "```" + (lang ?? '');
        const body = document.createElement('pre')
        const code = document.createElement('code');
        code.classList.add(`language-${lang}`);
        code.innerText = content;
        body.appendChild(code);
        hljs.highlightElement(code);
        const footer = document.createElement('span')
        footer.innerText = "```";
        div.appendChild(header)
        div.appendChild(body)
        div.appendChild(footer)
        return div;
    }

    #codePart(content) {
        const div = document.createElement('span')
        div.classList.add('code')
        div.innerText = `\`${content}\``;
        return div;
    }

    #boldPart(content) {
        const div = document.createElement('b')
        div.classList.add('bold')
        div.innerText = `**${content}**`;
        return div;
    }

    #italicPart(content) {
        const div = document.createElement('i')
        div.classList.add('italic')
        div.innerText = `*${content}*`;
        return div;
    }

    #emotePart(content) {
        const customEmote = getCustomEmoji().find(e => e.data === `:${content}:`)
        const div = document.createElement('span')
        if (customEmote) {
            div.innerHTML = customEmote.content;
        } else {
            div.innerText = `:${content}:`;
        }
        div.classList.add('emote')
        return div;
    }

    #userAtPart(content) {
        const user = RVC.room.currentUsers.find(u => u.id === content);
        const div = document.createElement('span')
        if (user) {
            div.classList.add('mention')
            div.innerText = `@${user.displayName} `;
        } else {
            div.innerText = `<@userId:${content}>`;
        }
        return div;
    }

    #roleAtPart(content) {
        const role = RVC.room.currentRoles.find(r => r.id === content);
        const div = document.createElement('span')
        if (role) {
            div.classList.add('mention')
            div.innerText = `@${role.name} `;
        } else {
            div.innerText = `<@roleId:${content}>`;
        }
        return div;
    }

    #handlePreviewClick(e) {
        let rawOffset = 0;
        const children = Array.from(this.textPreview.childNodes);
        for (const child of children) {
            if (child.classList?.contains('cursor-caret')) continue;
            if (child === e.target || child.contains?.(e.target)) {
                const innerOffset = this.#offsetWithinElement(child, e.clientX, e.clientY);
                rawOffset += Math.min(innerOffset, child._rawLength ?? 0);
                break;
            }
            rawOffset += child._rawLength ?? 0;
        }

        this.textInput.focus();
        this.textInput.setSelectionRange(rawOffset, rawOffset);
        this.#syncMessage();
    }

    /**
     * Finds the closest character offset to (x, y) within `root`.
     *
     * We deliberately avoid {@link document.caretRangeFromPoint} / {@link document.caretPositionFromPoint} here:
     * Both are unreliable across a shadow DOM boundary — in Chrome and
     * Firefox they frequently resolve to the shadow host element instead of the
     * actual text node inside the shadow tree, which silently breaks offset
     * lookups (the caller can't find the returned node inside `root`, so it
     * falls back to "end of element" every time). Since we already know which
     * child was clicked (via {@link e.target}), we hit-test per character instead using
     * {@link Range.getBoundingClientRect()}, which works fine on nodes we already hold
     * a reference to, shadow DOM or not.
     */
    #offsetWithinElement(root, x, y) {
        const walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT);
        let count = 0, n;
        let closest = { dist: Infinity, offset: 0 };

        while ((n = walker.nextNode())) {
            const result = this.#scanTextNode(n, x, y, count);
            if (result.exact !== null) {
                return result.exact;
            }
            if (result.closest.dist < closest.dist) {
                closest = result.closest;
            }
            count += n.textContent.length;
        }
        return closest.dist === Infinity ? count : closest.offset;
    }

    /** Scans a single text node character-by-character for a hit at (x, y).
     *  `base` is the running offset of this node's start within the root element. */
    #scanTextNode(node, x, y, base) {
        const range = document.createRange();
        const text = node.textContent;
        let closest = { dist: Infinity, offset: base };

        for (let i = 0; i < text.length; i++) {
            const rect = this.#charRect(range, node, i);
            if (!rect) continue;

            const mid = rect.left + rect.width / 2;
            const onLine = y >= rect.top && y <= rect.bottom;

            if (onLine && x <= mid) {
                return { exact: base + i, closest };
            }
            if (onLine && x <= rect.right) {
                return { exact: base + i + 1, closest };
            }

            const dist = Math.hypot(x - mid, y - (rect.top + rect.height / 2));
            if (dist < closest.dist) {
                closest = { dist, offset: x <= mid ? base + i : base + i + 1 };
            }
        }
        return { exact: null, closest };
    }

    /** Bounding rect of the single character at offset `i` in `node`, or null if empty. */
    #charRect(range, node, i) {
        range.setStart(node, i);
        range.setEnd(node, i + 1);
        const rect = range.getBoundingClientRect();
        return (rect.width || rect.height) ? rect : null;
    }

    #removeCursor() {
        this.shadowRoot.querySelectorAll('.cursor-caret').forEach(el => el.remove());
    }
}

customElements.define('revoice-textarea', TextareaComponent);