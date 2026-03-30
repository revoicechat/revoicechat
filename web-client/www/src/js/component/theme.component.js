class ThemePreviewComponent extends HTMLElement {

  connectedCallback() {
    const dataTheme = this.getAttribute("theme")
    this.innerHTML = `
      <link href="src/js/component/theme.component.css" rel="stylesheet" />
      <div class="data-theme-holder">
          <div data-theme="${dataTheme}">
              <div class="data-theme-container">
                  <div class="data-theme-left">
                      <div class="data-theme-left-name"></div>
                      <div class="data-theme-left-list"></div>
                      <div class="data-theme-left-user"></div>
                  </div>
                  <div class="data-theme-center">
                      <div class="data-theme-center-name"></div>
                      <div class="data-theme-center-messages">
                          <div class="data-theme-message">message 1</div>
                          <div class="data-theme-message">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</div>
                          <div class="data-theme-message">message 2</div>
                          <div class="data-theme-message">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</div>
                      </div>
                      <div class="data-theme-center-input">
                          <div class="data-theme-message-input">insert message</div>
                      </div>
                  </div>
                  <div class="data-theme-right">
                      <div class="data-theme-right-name"></div>
                      <div class="data-theme-right-list">
                          <div class="data-theme-button" style="background-color: var(--pri-button-bg-color)">button 1</div>
                          <div class="data-theme-button" style="background-color: var(--pri-button-hover-color)">button 1 hover</div>
                          <div class="data-theme-button" style="background-color: var(--sec-button-bg-color)">button 2</div>
                          <div class="data-theme-button" style="background-color: var(--sec-button-hover-color)">button 2 hover</div>
                          <div class="data-theme-button" style="background-color: var(--ter-button-hover-color)">button 3</div>
                          <div class="data-theme-button" style="background-color: var(--ter-button-hover-color)">button 3 hover</div>
                      </div>
                      <div class="data-theme-right-last"></div>
                  </div>
              </div>
          </div>
          <div style="text-align: center">${dataTheme}</div>
      </div>
                `;
  }
}

customElements.define('revoice-theme-preview', ThemePreviewComponent);

/**
 * @return {string[]}
 */
function getDataThemesFromDOM() {
  return Array.from(new Set(
      Array.from(document.querySelectorAll('[data-theme]'))
          .map(el => el.dataset.theme || "")
          .flatMap(v => v.split(/\s+/))
          .filter(Boolean)
  ));
}

/**
 * @return {string[]}
 */
function getDataThemesFromStylesheets() {
  const themes = new Set();
  const regex = /\[data-theme\s*=\s*(?:"([^"\]]+)"|'([^'\]]+)'|([^\]\s]+))]/g;

  for (const sheet of document.styleSheets) {
    let rules;
    try {
      rules = sheet.cssRules;
    } catch {
      continue; // skip cross-origin
    }
    if (!rules) continue;

    const checkRules = (ruleList) => {
      for (const rule of ruleList) {
        if (rule.selectorText) {
          let match;
          while ((match = regex.exec(rule.selectorText)) !== null) {
            themes.add(match[1]);
          }
        }
        if (rule.cssRules) checkRules(rule.cssRules); // handle nested @media
      }
    };
    checkRules(rules);
  }
  return Array.from(themes);
}

function getAllDeclaredDataThemes() {
  return Array.from(new Set([
    ...getDataThemesFromDOM(),
    ...getDataThemesFromStylesheets()
  ])).sort((a, b) => a.localeCompare(b));
}

export {getAllDeclaredDataThemes}