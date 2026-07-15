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
    #mentionStart = null;

    /** @type HTMLPreElement */
    textPreview;
    /** @type HTMLTextAreaElement */
    textInput;
    /** @type HTMLDivElement */
    mentionList;

    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.mentionList = document.createElement('div')
        this.mentionList.classList.add('mention-list', 'hidden')
        this.#initStyle();
        this.#initTextInput();
        this.#initTextPreview();
        this.shadowRoot.appendChild(this.mentionList);
        this.shadowRoot.appendChild(this.textInput);
        this.shadowRoot.appendChild(this.textPreview);

        this.textInput.addEventListener('keydown', (e) => this.#handleKeyDown(e));
        this.textInput.addEventListener('input', () => this.#syncMessage());
        this.textInput.addEventListener('paste', () => this.#syncMessage());
        this.textInput.addEventListener('keyup',  () => this.#syncMessage());
        this.textInput.addEventListener('mouseup',() => this.#syncMessage());
        this.textInput.addEventListener('focus',   () => this.#syncMessage());
        this.textInput.addEventListener('blur',   () => {
            this.#cursorPos = null;
            this.#removeCursor();
            this.#hideMentionList();
        });
        this.textPreview.addEventListener('click', (e) => this.#handlePreviewClick(e));

        // Prevent mousedown on the list from stealing focus (and blurring the
        // textarea) before the click that actually picks a user/role fires.
        this.mentionList.addEventListener('mousedown', (e) => e.preventDefault());
        this.mentionList.addEventListener('click', (e) => this.#handleMentionListClick(e));
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
        this.textInput.id = 'text-input';
        this.textInput.rows = 1;
    }

    #syncMessage() {
        this.#cursorPos = this.textInput.selectionStart;
        this.#clampCursor();
        this.#render();
        this.#updatePlaceholder(true)
        this.#updateMentionList();
    }

    /**
     * If the cursor ended up strictly inside a userAt/roleAt mention (e.g. after a
     * click or a mouse-drag selection collapsing), snap it to whichever boundary
     * of the mention (start or end) is closer, so the caret can never rest inside
     * the tag itself.
     */
    #clampCursor() {
        if (this.#cursorPos === null) return;
        const value = this.textInput.value;
        const mention = this.#findMentionAt(value, this.#cursorPos);
        if (!mention) return;

        const target = (this.#cursorPos - mention.start <= mention.end - this.#cursorPos)
            ? mention.start
            : mention.end;

        this.#cursorPos = target;
        if (this.textInput.selectionStart !== target || this.textInput.selectionEnd !== target) {
            this.textInput.setSelectionRange(target, target);
        }
    }

    /**
     * Looks for an in-progress `@query` right before the cursor (must start at the
     * beginning of the text or right after whitespace, and contain only word chars
     * so a space kills the query). Shows/filters/hides the mention list accordingly.
     */
    #updateMentionList() {
        if (this.#cursorPos === null || !this.textInput.matches(':focus')) {
            this.#hideMentionList();
            return;
        }

        const before = this.textInput.value.slice(0, this.#cursorPos);
        const match = before.match(/(?:^|\s)@(\w*)$/);
        if (!match) {
            this.#hideMentionList();
            return;
        }

        this.#mentionStart = this.#cursorPos - match[1].length - 1;
        this.#renderMentionList(match[1]);
    }

    #renderMentionList(query) {
        const q = query.toLowerCase();
        const users = (RVC.room.currentUsers ?? [])
            .filter(u => u.displayName.toLowerCase().includes(q))
            .map(u => ({ type: 'userAt', id: u.id, label: u.displayName }));
        const roles = (RVC.room.currentRoles ?? [])
            .filter(r => r.name.toLowerCase().includes(q))
            .map(r => ({ type: 'roleAt', id: r.id, label: r.name }));
        const items = [...users, ...roles];

        if (items.length === 0) {
            this.#hideMentionList();
            return;
        }

        this.mentionList.innerHTML = '';
        for (const item of items) {
            const entry = document.createElement('div');
            entry.classList.add('mention-list-item');
            entry.textContent = `@${item.label}`;
            entry.dataset.type = item.type;
            entry.dataset.id = item.id;
            this.mentionList.appendChild(entry);
        }
        this.mentionList.classList.remove('hidden');
    }

    #hideMentionList() {
        this.#mentionStart = null;
        this.mentionList.classList.add('hidden');
        this.mentionList.innerHTML = '';
    }

    #handleMentionListClick(e) {
        const entry = e.target.closest('[data-id]');
        if (!entry || this.#mentionStart === null) return;

        const tag = entry.dataset.type === 'roleAt'
            ? `<@&roleId:${entry.dataset.id}>`
            : `<@userId:${entry.dataset.id}>`;

        const value = this.textInput.value;
        const queryEnd = this.#cursorPos ?? this.textInput.selectionStart;
        const newValue = value.slice(0, this.#mentionStart) + tag + value.slice(queryEnd);
        const newPos = this.#mentionStart + tag.length;

        this.textInput.value = newValue;
        this.textInput.focus();
        this.textInput.setSelectionRange(newPos, newPos);
        this.#hideMentionList();
        this.#syncMessage();
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

    /**
     * Intercepts Backspace so that deleting right after a `<@userId:...>` or
     * `<@&roleId:...>` mention removes the whole token instead of one raw character.
     */
    #handleKeyDown(e) {
        switch (e.key) {
            case 'Backspace': return this.#handleBackspace(e);
            case 'Delete':    return this.#handleDelete(e);
            case 'ArrowLeft': return this.#handleArrowStep(e, -1);
            case 'ArrowRight':return this.#handleArrowStep(e, 1);
        }
    }

    #handleBackspace(e) {
        const start = this.textInput.selectionStart;
        const end = this.textInput.selectionEnd;
        if (start !== end) return; // there's a selection, let the browser handle it normally

        const value = this.textInput.value;
        const mention = this.#findMentionEndingAt(value, start);
        if (!mention) return;

        e.preventDefault();
        this.#deleteRange(mention.start, end);
    }

    #handleDelete(e) {
        const start = this.textInput.selectionStart;
        const end = this.textInput.selectionEnd;
        if (start !== end) return; // there's a selection, let the browser handle it normally

        const value = this.textInput.value;
        const mention = this.#findMentionStartingAt(value, start);
        if (!mention) return;

        e.preventDefault();
        this.#deleteRange(start, mention.end);
    }

    /**
     * Left/Right arrow: if the plain next-character move would land the caret
     * strictly inside a mention, jump clean over the whole tag instead so the
     * caret can only ever rest at a mention's start or end.
     */
    #handleArrowStep(e, delta) {
        if (e.shiftKey || e.ctrlKey || e.metaKey || e.altKey) return; // leave selection/word-jump to the browser
        const start = this.textInput.selectionStart;
        const end = this.textInput.selectionEnd;
        if (start !== end) return; // leave collapsing a selection to the browser

        const value = this.textInput.value;
        const mention = this.#findMentionAt(value, start + delta);
        if (!mention) return;

        e.preventDefault();
        const target = delta < 0 ? mention.start : mention.end;
        this.textInput.setSelectionRange(target, target);
        this.#syncMessage();
    }

    #deleteRange(start, end) {
        const value = this.textInput.value;
        this.textInput.value = value.slice(0, start) + value.slice(end);
        this.textInput.setSelectionRange(start, start);
        this.#syncMessage();
    }

    /**
     * Looks for a userAt/roleAt mention whose match ends exactly at `pos` in `value`.
     * Returns {start, end} of the mention (raw markdown, e.g. `<@userId:...>`) or null.
     */
    #findMentionEndingAt(value, pos) {
        return this.#findMention(value, (s, e) => e === pos);
    }

    /**
     * Looks for a userAt/roleAt mention whose match starts exactly at `pos` in `value`.
     */
    #findMentionStartingAt(value, pos) {
        return this.#findMention(value, (s) => s === pos);
    }

    /**
     * Looks for a userAt/roleAt mention that strictly contains `pos` (not counting
     * its own start/end boundaries).
     */
    #findMentionAt(value, pos) {
        return this.#findMention(value, (s, e) => pos > s && pos < e);
    }

    #findMention(value, predicate) {
        for (const re of [userAt, roleAt]) {
            re.lastIndex = 0;
            let match;
            while ((match = re.exec(value)) !== null) {
                const matchStart = match.index;
                const matchEnd = matchStart + match[0].length;
                if (predicate(matchStart, matchEnd)) {
                    return { start: matchStart, end: matchEnd };
                }
                if (match[0].length === 0) re.lastIndex++;
            }
        }
        return null;
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