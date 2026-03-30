/**
 * Simple internationalization system for vanilla JS
 * Uses .properties files for translations
 */

class I18n {
    translationsLoaded = false;

    /** @param {string} translationDir */
    constructor(translationDir) {
        /** @type {Object} */
        this.translations = {};
        this.translationDir = translationDir;
        this.observers = new Map(); // Store MutationObservers for dynamic values
    }

    /**
     * Parse a .properties file into a JavaScript object
     * @param {string} content - Content of the .properties file
     * @param {Object} result - Content of the .properties file
     * @returns {Object} Object with key/value pairs
     */
    parseProperties(content, result = {}) {
        const lines = content.split('\n');

        for (const line of lines) {
            const trimmed = line.trim();
            // Ignore empty lines and comments
            if (!trimmed || trimmed.startsWith('#') || trimmed.startsWith('!')) {
                continue;
            }
            // Find first = or :
            const separatorIndex = trimmed.search(/[=:]/);
            if (separatorIndex === -1) continue;
            const key = trimmed.substring(0, separatorIndex).trim();
            result[key] = trimmed.substring(separatorIndex + 1).trim();
        }

        return result;
    }

    /**
     * Load a .properties file
     * @param {string} lang - Language code (e.g. 'fr', 'en')
     * @returns {Promise<Object>} Loaded translations
     */
    async loadTranslations(lang) {
        try {
            const files = await this.#getTranslationFiles()
            const values = {}
            await this.#loadTranslationsValues("en", files, values)
            if (lang !== "en") {
                await this.#loadTranslationsValues(lang, files, values)
            }
            return values;
        } finally {
            this.translationsLoaded = true
        }
    }

    /**
     * @param {String}   lang
     * @param {String[]} files
     * @param {Object}   properties
     */
    async #loadTranslationsValues(lang, files, properties) {
        for (const file of files) {
            const response = await fetch(`${this.translationDir}/${file}_${lang}.properties`);
            if (response.ok) {
                const content = await response.text();
                this.parseProperties(content, properties)
            } else if (lang === 'en') {
                throw new Error(`File ${file}_${lang}.properties not found`);
            } else {
                console.warn(`Language ${lang} not found, falling back to English`);
                const response = await fetch(`${this.translationDir}/${file}_en.properties`);
                const content = await response.text();
                return this.parseProperties(content, properties);
            }
        }
    }

    async #getTranslationFiles() {
        const response = await fetch(`${this.translationDir}/localizations.txt`);
        if (!response.ok) {
            throw new Error(`File files.txt not found`);
        }
        const content = await response.text();
        return content.split("\n").map(str => str.trim()).filter(str => str !== "");
    }

    /**
     * Replace placeholders in translation string
     * @param {string} template - Translation string with {0}, {1}, etc.
     * @param {Array|string} values - Values to replace placeholders
     * @returns {string} Formatted string
     */
    formatString(template, values) {
        if (!template) return '';
        template = template.replace(String.raw`\n`, "\n");
        // Convert single value to array
        const valueArray = Array.isArray(values) ? values : [values];

        // Replace {0}, {1}, {2}, etc.
        return template.replaceAll(/\{(\d+)}/g, (match, index) => {
            const idx = Number.parseInt(index);
            return valueArray[idx] === undefined ? match : valueArray[idx];
        });
    }

    /**
     * Translate an element with dynamic values
     * @param {HTMLElement} element - Element to translate
     */
    translateElement(element) {
        const key = element.dataset.i18n;
        const valueAttr = element.dataset.i18nValue;
        const translation = this.translations[key];

        if (!translation) {
            console.warn(`Missing translation for key: ${key}`);
            return;
        }

        // If there's a value attribute, parse and format
        if (valueAttr) {
            try {
                // Parse value (can be a single value or JSON array)
                const values = valueAttr.startsWith('[')
                        ? JSON.parse(valueAttr)
                        : valueAttr;

                element.textContent = this.formatString(translation, values);
            } catch (error) {
                console.error(`Error parsing data-i18n-value for key ${key}:`, error);
                element.textContent = translation;
            }
        } else {
            element.textContent = translation.replaceAll(String.raw`\n`, "\n");
        }
    }

    /**
     * Setup observer for dynamic value changes
     * @param {HTMLElement|ShadowRoot} element - Element to observe
     */
    observeElement(element) {
        // If already observing, disconnect first
        if (this.observers.has(element)) {
            this.observers.get(element).disconnect();
        }

        const observer = new MutationObserver((mutations) => {
            for (const mutation of mutations) {
                if (mutation.type === 'attributes' && mutation.attributeName === 'data-i18n-value') {
                    this.translateElement(element);
                }
            }
        });

        observer.observe(element, {
            attributes: true,
            attributeFilter: ['data-i18n-value']
        });

        this.observers.set(element, observer);
    }

    /**
     * Translate all elements with the data-i18n attribute
     * @param {Document|HTMLElement|ShadowRoot} doc
     */
    translatePage(doc = document) {
        if (!this.translationsLoaded) {
            return
        }
        // Translate text content with dynamic values support
        const elements = this.#querySelectorAllDeep('[data-i18n]', doc);
        for (const element of elements) {
            this.translateElement(element);

            // Setup observer if element has data-i18n-value
            if (Object.hasOwn(element.dataset, 'i18nValue')) {
                this.observeElement(element);
            }
        }

        // Translate title attributes (tooltips)
        const titledElements = doc.querySelectorAll('[data-i18n-title]');
        for (const element of titledElements) {
            const key = element.dataset.i18nTitle;
            const translation = this.translations[key];

            if (translation) {
                element.setAttribute('title', translation);
            } else {
                console.warn(`Missing translation for title key: ${key}`);
            }
        }

        // Translate placeholder attributes
        const placeholderElements = doc.querySelectorAll('[data-i18n-placeholder]');
        for (const element of placeholderElements) {
            const key = element.dataset.i18nPlaceholder
            const translation = this.translations[key];

            if (translation) {
                element.setAttribute('placeholder', translation);
            } else {
                console.warn(`Missing translation for placeholder key: ${key}`);
            }
        }
    }

    /**
     * Translate all elements with the data-i18n attribute
     * @param {string} selector
     * @param {Document|HTMLElement|ShadowRoot} root
     */
    #querySelectorAllDeep(selector, root) {
        const results = [];

        /** @param {Document|HTMLElement|ShadowRoot} node */
        function search(node) {
            // 1. Normal DOM elements
            if (node.querySelectorAll) {
                results.push(...node.querySelectorAll(selector));
            }

            // 2. Explore shadow DOMs
            const treeWalker = document.createTreeWalker(node, NodeFilter.SHOW_ELEMENT);

            let current = treeWalker.currentNode;
            while (current) {
                if (current.shadowRoot) {
                    // Search inside the shadow root
                    search(current.shadowRoot);
                }
                current = treeWalker.nextNode();
            }
        }

        search(root);
        return results;
    }

    /**
     * Main method to change language
     * @param {string} lang - Language code (e.g. 'fr', 'en')
     */
    async translate(lang) {
        try {
            this.translations = await this.loadTranslations(lang);
            this.translatePage();
            console.log(`Language changed to: ${lang}`);
        } catch (error) {
            console.error('Error loading translations:', error);
        }
    }

    /**
     * Get a translation by key (useful for dynamic JS)
     * @param {string} key - Translation key
     * @param {Array|string} values - Optional values for placeholders
     * @returns {string} Translation or the key if not found
     */
    translateOne(key, values = null) {
        if (!this.translationsLoaded) {
            return key
        }
        const translation = this.translations[key] || key;

        if (values !== null) {
            return this.formatString(translation, values);
        }

        return translation.replaceAll(String.raw`\n`, "\n");
    }

    /**
     * Update the value of an element and trigger re-translation
     * @param {string|HTMLElement} elementOrId - Element or element ID
     * @param {string|Array} value - New value(s) for translation
     */
    updateValue(elementOrId, value) {
        const element = typeof elementOrId === 'string'
                ? document.getElementById(elementOrId)
                : elementOrId;

        if (typeof elementOrId === 'string' && !element) {
            console.warn(`Element not found: ${elementOrId}`);
            return;
        }

        // Set the new value
        element.dataset.i18nValue = Array.isArray(value) ? JSON.stringify(value) : String(value);

        // The MutationObserver will automatically trigger re-translation
    }

    /**
     * Cleanup all observers
     */
    destroy() {
        for (const observer of this.observers) {
            observer[1].disconnect();
        }
        this.observers.clear();
    }
}

// Export for usage
const i18n = new I18n("src/i18n");

export {I18n, i18n}

// Usage examples:
// i18n.translate('fr');
// i18n.translate('en');
// i18n.t('audio.volume.label', '75'); // Returns: "Volume 75%"
// i18n.updateValue('input-volume-label', '80'); // Updates element value and re-translates