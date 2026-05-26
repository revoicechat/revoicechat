const codeBlock = /^```(\w+)?\n([\s\S]*?)^```$/gm
const code      = /`([^`]+)`/g;
const bold      = /\*\*(.+?)\*\*/g;
const italic    = /(?<!\*)\*(?!\*)(.+?)(?<!\*)\*(?!\*)/g;
const emote     = /:(\w+):/g;
const userAt    = /<@userId:([0-9a-fA-F\-]{36})>/g;
const roleAt    = /<@&roleId:([0-9a-fA-F\-]{36})>/g;

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
        switch (part.type) {
            case 'text':      return this.#textPart(part.content);
            case 'codeBlock': return this.#codeBlockPart(part.content, part.lang);
            case 'code':      return this.#codePart(part.content);
            case 'bold':      return this.#boldPart(part.content);
            case 'italic':    return this.#italicPart(part.content);
            case 'emote':     return this.#emotePart(part.content);
            case 'userAt':    return this.#userAtPart(part.content);
            case 'roleAt':    return this.#roleAtPart(part.content);
        }
    }

    #updatePlaceholder() {
        this.textPreview.classList.remove('placeholder')
        if (!this.textInput.value) {
            this.textPreview.innerHTML = `Send a message`;
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
        const div = document.createElement('span')
        div.classList.add('emote')
        div.innerText = `:${content}:`;
        return div;
    }

    #userAtPart(content) {
        const div = document.createElement('span')
        div.classList.add('mention')
        div.innerText = `@${content} `;
        return div;
    }

    #roleAtPart(content) {
        const div = document.createElement('span')
        div.classList.add('mention')
        div.innerText = `@${content} `;
        return div;
    }

    #handlePreviewClick(e) {
        let rawOffset = 0;
        const children = Array.from(this.textPreview.childNodes);
        for (const child of children) {
            if (child.classList?.contains('cursor-caret')) continue;
            if (child === e.target || child.contains?.(e.target)) {
                // Drill inside this element to get sub-offset
                let innerOffset = 0;
                const caret = document.caretRangeFromPoint?.(e.clientX, e.clientY)
                    ?? document.caretPositionFromPoint?.(e.clientX, e.clientY);
                if (caret) {
                    const node   = caret.startContainer ?? caret.offsetNode;
                    const offset = caret.startOffset    ?? caret.offset;
                    innerOffset  = this.#innerTextOffset(child, node, offset);
                }
                rawOffset += Math.min(innerOffset, child._rawLength ?? 0);
                break;
            }
            rawOffset += child._rawLength ?? 0;
        }

        this.textInput.focus();
        this.textInput.setSelectionRange(rawOffset, rawOffset);
    }

    #innerTextOffset(root, targetNode, offset) {
        if (root === targetNode) return offset;
        const walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT);
        let count = 0, n;
        while ((n = walker.nextNode())) {
            if (n === targetNode) {
                return count + offset;
            }
            count += n.textContent.length;
        }
        return count;
    }

    #updateCursor() {
        this.#removeCursor();
        const pos = this.textInput.selectionStart;
        if (pos === null || !this.textInput.matches(':focus')) return;
        let remaining = pos;
        const children = Array.from(this.textPreview.childNodes);
        for (let i = 0; i < children.length; i++) {
            const child = children[i];
            if (child.classList?.contains('cursor-caret')) continue;
            const len = child._rawLength ?? child.textContent.length;
            const isLast = (i === children.length - 1);
            // Enter this element if cursor is inside it, or it's the last one
            if (remaining < len || (isLast && remaining <= len)) {
                this.#insertCursorInElement(child, remaining);
                return;
            }
            remaining -= len;
        }
        const caret = this.#makeCaret();
        this.textPreview.appendChild(caret);
    }

    #insertCursorInElement(el, charOffset) {
        if (el.childNodes.length === 1 && el.childNodes[0].nodeType === Node.TEXT_NODE) {
            const textNode = el.childNodes[0];
            const range = document.createRange();
            const offset = Math.min(charOffset, textNode.textContent.length);
            range.setStart(textNode, offset);
            range.collapse(true);
            range.insertNode(this.#makeCaret());
            return;
        }
        const walker = document.createTreeWalker(el, NodeFilter.SHOW_TEXT);
        let remaining = charOffset;
        let n;
        while ((n = walker.nextNode())) {
            const len = n.textContent.length;
            if (remaining <= len) {
                const range = document.createRange();
                range.setStart(n, remaining);
                range.collapse(true);
                range.insertNode(this.#makeCaret());
                return;
            }
            remaining -= len;
        }
        el.appendChild(this.#makeCaret());
    }

    #makeCaret() {
        const caret = document.createElement('span');
        caret.className = 'cursor-caret';
        return caret;
    }

    #removeCursor() {
        this.shadowRoot.querySelectorAll('.cursor-caret').forEach(el => el.remove());
    }
}

class MarkdownPart {
    /** @type {"text"|"code"} */
    type;
    /** @type {string|null} */
    lang;
    /** @type {string} */
    content;
}

customElements.define('revoice-textarea', TextareaComponent);
