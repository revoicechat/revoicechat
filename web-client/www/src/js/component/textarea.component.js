const codeBlock = /^```(\w+)?\n([\s\S]*?)^```$/gm
const code      = /`([^`]+)`/g;
const bold      = /\*\*(.+?)\*\*/g;
const italic    = /(?<!\*)\*(?!\*)(.+?)(?<!\*)\*(?!\*)/g;
const emote     = /:(\w+):/g;
const userAt    = /<@userId:(\w+)>/g;
const roleAt    = /<@&roleId:(\w+)>/g;

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

    /** @type HTMLPreElement */
    textPreview;
    /** @type HTMLTextAreaElement */
    textInput;

    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.#initStyle();
        this.#initTextPreview();
        this.#initTestInput();
        this.shadowRoot.appendChild(this.textInput);
        this.shadowRoot.appendChild(this.textPreview);

        this.textInput.addEventListener('input', () => this.#syncMessage());
        this.textInput.addEventListener('paste', () => this.#syncMessage());
        // Cursor tracking
        this.textInput.addEventListener('keyup',  () => this.#updateCursor());
        this.textInput.addEventListener('mouseup',() => this.#updateCursor());
        this.textInput.addEventListener('blur',   () => this.#removeCursor());
        // Click on preview → move text area cursor
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

    #initTestInput() {
        this.textInput = document.createElement('textarea');
        this.textInput.style.opacity = '1';
        this.textInput.style.position = 'absolute';
        this.textInput.style.top = '-120px';
        this.textInput.id = 'text-input';
        this.textInput.rows = 1;
    }

    #syncMessage() {
        this.textPreview.innerHTML = '';
        const message = this.#splitMarkdown(this.textInput.value);
        for (const part of message) {
            let el;
            switch (part.type) {
                case 'text':      el = this.#textPart(part.content);                 break;
                case 'codeBlock': el = this.#codeBlockPart(part.content, part.lang); break;
                case 'code':      el = this.#codePart(part.content);                 break;
                case 'bold':      el = this.#boldPart(part.content);                 break;
                case 'italic':    el = this.#italicPart(part.content);               break;
                case 'emote':     el = this.#emotePart(part.content);                break;
                case 'userAt':    el = this.#userAtPart(part.content);               break;
                case 'roleAt':    el = this.#roleAtPart(part.content);               break;
            }
            if (el) {
                el._rawLength = part._rawLength;
                this.textPreview.appendChild(el);
            }
        }
        this.#updatePlaceholder();
        this.#updateCursor();
    }

    #updatePlaceholder() {
        this.textPreview.classList.remove('placeholder')
        if (!this.textPreview.innerHTML) {
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
        // Which top-level child of textPreview was clicked (or closest ancestor that is)?
        const children = Array.from(this.textPreview.childNodes)
            .filter(n => n.nodeType === Node.ELEMENT_NODE || n.nodeType === Node.TEXT_NODE);

        // Count raw chars before the clicked element
        let rawOffset = 0;
        for (const child of children) {
            if (child === e.target || child.contains(e.target)) {
                // We're inside this element — use caretRangeFromPoint to get sub-offset
                // within this element's visible text
                let innerOffset = 0;
                if (document.caretRangeFromPoint) {
                    const range = document.caretRangeFromPoint(e.clientX, e.clientY);
                    if (range) {
                        // Walk text nodes inside this child to find offset
                        innerOffset = this.#innerTextOffset(child, range.startContainer, range.startOffset);
                    }
                } else if (document.caretPositionFromPoint) {
                    const pos = document.caretPositionFromPoint(e.clientX, e.clientY);
                    if (pos) {
                        innerOffset = this.#innerTextOffset(child, pos.offsetNode, pos.offset);
                    }
                }
                // Clamp inner offset to this element's raw length
                rawOffset += Math.min(innerOffset, child._rawLength ?? child.textContent.length);
                break;
            }
            rawOffset += child._rawLength ?? child.textContent.length;
        }

        this.textInput.focus();
        this.textInput.setSelectionRange(rawOffset, rawOffset);
        this.#updateCursor();
    }

    // Walk text nodes inside `root` to count chars before `targetNode:offset`
    #innerTextOffset(root, targetNode, offset) {
        const walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT);
        let count = 0;
        let n;
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
        if (pos === null) return;
        let remaining = pos;
        const children = Array.from(this.textPreview.childNodes)
            .filter(n => n.nodeType === Node.ELEMENT_NODE);

        for (const child of children) {
            const len = child._rawLength ?? child.textContent.length;
            if (remaining <= len) {
                this.#insertCursorInElement(child, remaining);
                return;
            }
            remaining -= len;
        }
        // End of preview
        const caret = this.#makeCaret();
        this.textPreview.appendChild(caret);
    }

    #insertCursorInElement(el, charOffset) {
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
