/** @typedef {{
 *   icon: "error"|"success"|null,
 *   title: string,
 *   text: string,
 *   html: string,
 *   width: string,
 *   showCancelButton: boolean,
 *   confirmButtonText: string,
 *   confirmButtonClass: string,
 *   allowOutsideClick: boolean?
 *   didOpen: () => void
 *   preConfirm: () => *
 * }} ModalOpt
 */
import {i18n} from "../lib/i18n.js";

class ModalOptions {
    /** @type {"error"|"success"|null} */
    icon;
    /** @type string */
    title;
    /** @type string */
    text;
    /** @type {string|HTMLElement} */
    html;
    /** @type string */
    width;
    /** @type boolean */
    showCancelButton;
    /** @type string */
    confirmButtonText;
    /** @type string */
    confirmButtonClass;
    /** @type {() => void} } */
    didOpen = () => {
    }
    /** @type {() => *} } */
    preConfirm = () => {
        return {}
    }

    /**
     * @param {ModalOpt} options
     * @returns {ModalOptions}
     */
    static of(options) {
        const modalOptions = new ModalOptions();
        modalOptions.icon = options.icon
        modalOptions.title = options.title
        modalOptions.text = options.text
        modalOptions.html = options.html
        modalOptions.width = options.width
        modalOptions.showCancelButton = options.showCancelButton
        modalOptions.confirmButtonText = options.confirmButtonText
        modalOptions.confirmButtonClass = options.confirmButtonClass
        modalOptions.didOpen = options.didOpen ?? (() => {
        })
        modalOptions.preConfirm = options.preConfirm ?? (() => {
            return {}
        })
        return modalOptions
    }
}

/** Custom Modal Implementation using native <dialog> */
export default class Modal {
    static #instance = new Modal();

    /**
     * @param {string} title
     * @param {string} text
     * @returns {Promise<{isConfirmed: boolean, data: *, isDismissed: boolean}>}
     */
    static async toggleError(title, text = "") {
        return await Modal.toggle({
            icon: 'error',
            title: title,
            text: text,
            showCancelButton: false,
        });
    }

    /**
     * @param {*} options
     * @returns {Promise<{
     *   isConfirmed: boolean,
     *   data: *,
     *   isDismissed: boolean
     * }>}
     */
    static async toggle(options) {
        return Modal.#instance.fire(options);
    }

    constructor(id = "custom-modal") {
        const dialogHTML = `
            <dialog id="${id}" class="custom-dialog">
                <div class="dialog-content">
                    <div class="dialog-icon"></div>
                    <h2 class="dialog-title"></h2>
                    <p class="dialog-text"></p>
                    <form method="dialog" class="dialog-buttons">
                        <button value="confirm" class="dialog-confirm" data-i18n="modal.ok">OK</button>
                        <button value="cancel" class="dialog-cancel" style="display: none;" data-i18n="modal.cancel">Cancel</button>
                    </form>
                </div>
            </dialog>
        `;

        document.body.insertAdjacentHTML('beforeend', dialogHTML);
        this.dialog = document.getElementById(id);

        // Add styles
        this.#addStyles();
    }

    #addStyles() {
        if (document.getElementById('dialog-styles')) return;
        document.head.insertAdjacentHTML(
            'beforeend',
            `<link id="dialog-styles" href="src/js/component/modal.component.css" rel="stylesheet" />`
        );
    }

    /**
     * @param {*} opt
     * @returns {Promise<{
     *   isConfirmed: boolean,
     *   data: *,
     *   isDismissed: boolean
     * }>}
     */
    fire(opt) {
        return new Promise((resolve) => {
            const options = ModalOptions.of(opt);
            // Set icon
            const iconEl = this.dialog.querySelector('.dialog-icon');
            iconEl.className = 'dialog-icon';
            if (options.icon) {
                iconEl.style.display = 'flex';
                switch (options.icon) {
                    case "success":
                        iconEl.innerHTML = `<revoice-icon-circle-check></revoice-icon-circle-check>`;
                        iconEl.classList.add("green");
                        break;
                    case "error":
                        iconEl.innerHTML = `<revoice-icon-circle-x></revoice-icon-circle-x>`;
                        iconEl.classList.add("red");
                        break;
                }
            } else {
                iconEl.style.display = 'none';
            }

            // Set width
            if (options.width) {
                this.dialog.querySelector('.dialog-content').style.width = options.width;
            }
            else{
                this.dialog.querySelector('.dialog-content').style.width = "auto";
            }

            // Set title
            this.dialog.querySelector('.dialog-title').textContent = options.title || '';

            // Set text
            const textEl = this.dialog.querySelector('.dialog-text');
            if (options.text) {
                textEl.textContent = options.text;
                textEl.style.display = 'block';
            } else if (options.html) {
                textEl.style.display = 'block';
                textEl.innerHTML = "";
                if (typeof options.html === 'string') {
                    textEl.innerHTML = options.html;
                } else {
                    textEl.appendChild(options.html)
                }
            } else {
                textEl.textContent = '';
                textEl.style.display = 'none';
            }

            // Set buttons
            const confirmBtn = this.dialog.querySelector('.dialog-confirm');
            const cancelBtn = this.dialog.querySelector('.dialog-cancel');

            confirmBtn.textContent = options.confirmButtonText || i18n.translateOne("modal.ok");
            if (options.confirmButtonClass) {
                confirmBtn.classList.add(options.confirmButtonClass)
            } else {
                confirmBtn.class = "dialog-confirm"
            }
            cancelBtn.style.display = options.showCancelButton ? 'inline-block' : 'none';

            // Handle dialog close
            const handleClose = (e) => {
                const returnValue = this.dialog.returnValue;
                this.dialog.removeEventListener('close', handleClose);

                if (returnValue === 'confirm') {
                    resolve({ isConfirmed: true, data: options.preConfirm() });
                } else {
                    resolve({ isConfirmed: false, isDismissed: true });
                }
                this.dialog.close();
            };

            this.dialog.addEventListener('close', handleClose, { once: true });

            options.didOpen();
            this.dialog.showModal();
        });
    }
}