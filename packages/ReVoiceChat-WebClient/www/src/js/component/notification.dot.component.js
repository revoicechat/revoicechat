class NotificationDotComponent extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
    }

    static get observedAttributes() {
        return ['mentions'];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if (name === 'mentions' && oldValue !== newValue) {
            this.#setupDOM();
        }
    }

    /** generate the data in slot */
    connectedCallback() {
        this.#setupDOM()
    }

    #setupDOM() {
        const mentions = this.mentionsAttribute
        let digitClasses
        let value
        if (mentions === 0) {
            digitClasses = ''
            value = ''
        } else if (mentions < 10) {
            digitClasses = 'mentions one-digit'
            value = mentions
        } else if (mentions < 100) {
            digitClasses = 'mentions two-digits'
            value = mentions
        } else {
            digitClasses = 'mentions three-digits'
            value = '99+'
        }
        this.shadowRoot.innerHTML = `
        <style>
            .dot {
                width:  0.6rem;
                height: 0.6rem;
                text-align: center;
                font-size: 0.8rem;
                font-weight: 800;
                background-color: var(--notification-color);
                margin-right: 0.1rem;
                border-radius: 9999px;
                
            }
            .mentions {
                height: 1.2rem;
                background-color: var(--notification-mention-color);
            }
            .one-digit {
                width:  1.2rem;
            }
            .two-digit {
                width:  1.6rem;
            }
            .three-digit {
                width:  2rem;
            }
        </style>
        <div id="dot" class="dot ${digitClasses}">
            ${value}
        </div>`
    }

    get mentionsAttribute() {
        const raw = this.getAttribute("mentions");
        return Number.isFinite(Number(raw)) ? Number(raw) : 0;
    }
}

customElements.define('revoice-notification-dot', NotificationDotComponent);