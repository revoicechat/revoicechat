class OpenGraphCard extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({mode: 'open'});
    }

    set ogdata(data) {
        this.shadowRoot.innerHTML = `
        <link href="src/js/component/opengraph.component.css" rel="stylesheet" />
        <div class="card">
          <div class="hero">
            <img src="${data?.image?.image}" alt="${data?.image?.alt}">
            <div class="hero-overlay"></div>
          </div>

          <div class="body">
            <div class="title field-val"><a href="${data?.basic?.url}" target="_blank">${data?.basic?.title}</a></div>
            <div class="site">${data?.page?.siteName}</div>
            <div class="description">${data?.page?.description}</div>
          </div>
        </div>
      `;

        // Lazy-load image fade-in
        this.shadowRoot.querySelectorAll('.hero img').forEach(img => {
            if (img.complete) img.classList.add('loaded');
            else img.addEventListener('load', () => img.classList.add('loaded'));
        });
    }
}

customElements.define('revoice-opengraph-card', OpenGraphCard);