/** Custom Modal Implementation using native <dialog> */
export default class FancyBox {
    static #instance = new FancyBox();

    /** @param {{src: string, alt: string, caption: string,}} options */
    static toggle(options) {
        FancyBox.#instance.fire(options);
    }

    constructor() {
        const dialogHTML = `
            <div id="custom-fancy-box-modal" class="custom-dialog">
                 <div class="modal-box">
                    <button class="modal-close" id="modal-close" aria-label="Close">
                        <revoice-icon-circle-x></revoice-icon-circle-x>
                    </button>
                    <img id="modal-img" src="" alt="" />
                    <p id="modal-caption"></p>
                    <div class="modal-toolbar">
                        <a id="btn-open" class="modal-btn" href="#" target="_blank" rel="noopener">
                            <svg viewBox="0 0 16 16"><path d="M8.636 3.5a.5.5 0 0 0-.5-.5H1.5A1.5 1.5 0 0 0 0 4.5v10A1.5 1.5 0 0 0 1.5 16h10a1.5 1.5 0 0 0 1.5-1.5V7.864a.5.5 0 0 0-1 0V14.5a.5.5 0 0 1-.5.5h-10a.5.5 0 0 1-.5-.5v-10a.5.5 0 0 1 .5-.5H8.136a.5.5 0 0 0 .5-.5z"/><path d="M16 .5a.5.5 0 0 0-.5-.5h-5a.5.5 0 0 0 0 1h3.793L6.146 9.146a.5.5 0 1 0 .708.708L15 1.707V5.5a.5.5 0 0 0 1 0V.5z"/></svg>
                            Open in browser
                        </a>
                        <a id="btn-download" class="modal-btn" href="#" download>
                            <svg viewBox="0 0 16 16"><path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/><path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z"/></svg>
                            Download
                        </a>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', dialogHTML);
        this.modal = document.getElementById('custom-fancy-box-modal');
        this.modalImg = document.getElementById('modal-img');
        this.modalCap = document.getElementById('modal-caption');
        this.btnOpen = document.getElementById('btn-open');
        this.btnDl = document.getElementById('btn-download');
        this.btnClose = document.getElementById('modal-close');

        this.btnClose.addEventListener('click', () => this.closeModal());
        this.modal.addEventListener('click', e => {
            if (e.target === this.modal) this.closeModal();
        });
        this.#addStyles()
    }

    #addStyles() {
        if (document.getElementById('dialog-fancy-box-styles')) return;
        document.head.insertAdjacentHTML(
            'beforeend',
            `<link id="dialog-fancy-box-styles" href="src/js/component/fancy.box.component.css" rel="stylesheet" />`
        );
    }

    /** @param {{src: string, alt: string, caption: string,}} opt */
    fire(opt) {
        this.modalImg.src = opt.src;
        this.modalImg.alt = opt.alt || '';
        this.modalCap.textContent = opt.caption || '';
        this.btnOpen.href = opt.src;
        this.btnDl.href = opt.src;
        const filename = opt.src.split('/').pop().split('?')[0] || 'image.jpg';
        this.btnDl.download = filename.includes('.') ? filename : filename + '.jpg';
        this.modal.classList.add('open');
        document.body.style.overflow = 'hidden';
        this.btnClose.focus();
    }

    closeModal() {
        this.modal.classList.remove('open');
        document.body.style.overflow = '';
        this.modalImg.src = '';
    }
}