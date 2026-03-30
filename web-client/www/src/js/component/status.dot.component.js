class DotComponent extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
    }

    static get observedAttributes() {
        return ['color'];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if (name === 'color' && oldValue !== newValue) {
            this.#setupDOM();
        }
    }

    /** generate the data in slot */
    connectedCallback() {
        this.#setupDOM()
    }

    #setupDOM() {
        this.shadowRoot.innerHTML = `
        <style>
            .background-green {
                background-color: #22c55e;
            }

            .background-orange {
                background-color: #fb883c;
            }

            .background-red {
                background-color: #ff0000;
            }

            .background-gray {
                background-color: #71717a;
            }

            .dot {
                position: absolute;
                right: -0.5rem;
                bottom: -0.45rem;
                width: 0.9rem;
                height: 0.9rem;
                border-style: solid;
                border-width: 2px;
                border-color: rgb(31 41 55);
                border-radius: 9999px;
                
            }
        </style>
        <div id="dot" class="dot background-${this.colorAttribute}">
        </div>`
    }

    get colorAttribute() {
        const color = this.getAttribute("color");
        return ["gray", "green", "orange", "red"].includes(color) ? color : "gray";
    }
}

customElements.define('revoice-status-dot', DotComponent);