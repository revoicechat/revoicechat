import {i18n} from "../lib/i18n.js";

export class LanguageController {

    static async loadAvailableLanguage() {
        const res = await fetch("src/i18n/lang.json");
        /** @type {Object} */
        const languages = await res.json();
        const select = document.getElementById("setting-language-selection")
        for (const key in languages) {
            const option = document.createElement('option');
            option.value     = key
            option.innerText = languages[key]
            option.selected  = (key === RVC.user.settings.getLanguage())
            select.appendChild(option);
        }
        select.addEventListener("change", (event) => {
            const value = event.target.value;
            RVC.user.settings.setLangage(value)
            RVC.user.settings.save();
            i18n.translate(value).then(() => RVC.user.settings.buildMessageExemple());
        });
    }
}