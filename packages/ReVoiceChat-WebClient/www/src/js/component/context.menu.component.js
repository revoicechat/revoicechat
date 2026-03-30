import { i18n } from "../lib/i18n.js";

class ContextMenu extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: "open" });
        this.onClickOutside = this.onClickOutside.bind(this);
    }

    open(x, y) {
        this.style.display = "block";

        const w = this.offsetWidth;
        const h = this.offsetHeight;
        const vw = innerWidth;
        const vh = innerHeight;

        // clamp inside viewport
        const left = Math.min(x, vw - w);
        const top = Math.min(y, vh - h);

        this.style.left = left + "px";
        this.style.top = top + "px";

        document.addEventListener("pointerdown", this.onClickOutside, true);

        // Close
        const btnClose = this.shadowRoot.getElementById("close")
        if(btnClose){
            btnClose.onclick = () => this.close();
        }
    }

    close() {
        this.style.display = "none";
        document.removeEventListener("pointerdown", this.onClickOutside, true);
    }

    onClickOutside(e) {
        if (!this.contains(e.target)) this.close();
    }
}

class VoiceContextMenu extends ContextMenu {
    #voiceCall;
    #voiceController;
    #userSettings;

    constructor() {
        super();
        this.shadowRoot.innerHTML = `
            <link href="src/css/main.css" rel="stylesheet" />
            <link href="src/css/themes.css" rel="stylesheet" />
            <link href="src/js/component/context.menu.component.css" rel="stylesheet" />

            <div class="menu">
                <div class="item slider" id="volume-block">
                    <label id="volume-label" data-i18n-value="0" data-i18n="voice.volume">Volume</label>
                    <input id="volume" type="range" min="0" max="2" step="0.01"></input>
                </div>
                
                <button class="item" id="mute" title="Mute"></button>

                <button class="item" id="close">
                    <span data-i18n="common.exit">Exit</span> <revoice-icon-circle-x></revoice-icon-circle-x> 
                </button>
            </div>
        `;
        i18n.translatePage(this);
    }

    #saveSettings() {
        if (this.#voiceCall) {
            this.#userSettings.voice = this.#voiceCall.getSettings();
        }

        this.#userSettings.save();
    }

    setVoiceCall(voiceCall) {
        this.#voiceCall = voiceCall;
    }

    load(userSettings, userId, voiceController) {
        this.#userSettings = userSettings;
        this.#voiceController = voiceController;
        const voiceSettings = userSettings.voice.users[userId];

        // Volume
        const volumeInput = this.shadowRoot.getElementById("volume");
        const volumeLabel = this.shadowRoot.getElementById("volume-label");

        // Initial volume
        volumeInput.value = voiceSettings.volume;
        volumeInput.title = Number.parseInt(voiceSettings.volume * 100) + "%";
        volumeLabel.dataset.i18nValue = volumeInput.title;

        volumeInput.oninput = () => {
            volumeInput.title = Number.parseInt(volumeInput.value * 100) + "%";
            volumeLabel.innerText = `Volume ${volumeInput.title}`;
            voiceSettings.volume = volumeInput.value;
            if (this.#voiceCall) {
                this.#voiceCall.updateUserVolume(userId);
            }
        }
        volumeInput.onchange = () => {
            this.#saveSettings();
        }

        const volumeBlock = this.shadowRoot.getElementById("volume-block");
        volumeBlock.ondblclick = () => {
            volumeInput.title = "100%";
            volumeLabel.dataset.i18nValue = volumeInput.title;
            volumeInput.value = 1;
            voiceSettings.volume = 1;
            this.#saveSettings();
            if (this.#voiceCall) {
                this.#voiceCall.updateUserVolume(userId);
            }
        }

        // Mute
        const muteButton = this.shadowRoot.getElementById("mute");
        muteButton.innerHTML = this.#getMuteHTML(voiceSettings);

        muteButton.onclick = async () => {
            voiceSettings.muted = !voiceSettings.muted;
            muteButton.innerHTML = this.#getMuteHTML(voiceSettings);
            this.#voiceController.updateUserExtension(userId);
            this.#saveSettings();
            if (this.#voiceCall) {
                await this.#voiceCall.setUserMute(userId, voiceSettings.muted);
            }
        }
    }

    #getMuteHTML(voiceSettings) {
        if (voiceSettings.muted) {
            return `Unmute <revoice-icon-speaker-x class="right red"></revoice-icon-speaker-x>`;
        }
        else {
            return `Mute <revoice-icon-speaker class="right"></revoice-icon-speaker>`;
        }
    }
}

class StreamContextMenu extends ContextMenu {
    #stream;
    #streamController;

    constructor() {
        super();
        this.shadowRoot.innerHTML = `
            <link href="src/css/main.css" rel="stylesheet" />
            <link href="src/css/themes.css" rel="stylesheet" />
            <link href="src/js/component/context.menu.component.css" rel="stylesheet" />

            <div class="menu">
                <button class="item" id="stop">Stop watching <revoice-icon-arrow-out></revoice-icon-arrow-out></button>

                <div class="item slider" id="volume-block">
                    <label id="volume-label" data-i18n-value="0" data-i18n="voice.volume">Volume</label>
                    <input id="volume" type="range" min="0" max="1" step="0.01" value="1"></input>
                </div>

                <button class="item" id="close">
                    <span data-i18n="common.exit">Exit</span> <revoice-icon-circle-x></revoice-icon-circle-x> 
                </button>
            </div>
        `;
        i18n.translatePage(this);
    }

    load(stream, streamController, userId, streamName) {
        this.#stream = stream;
        this.#streamController = streamController;

        // Stop watching
        this.shadowRoot.getElementById('stop').onclick = async () => {
            await this.#streamController.leave({ user: userId, name: streamName });
            this.close();
        }

        // Volume
        const volumeInput = this.shadowRoot.getElementById("volume");
        const volumeLabel = this.shadowRoot.getElementById("volume-label");

        // Initial volume
        if (stream) {
            volumeInput.value = stream.getVolume();
            volumeInput.title = Number.parseInt(volumeInput.value * 100) + "%";;
            volumeLabel.dataset.i18nValue = volumeInput.title;
        }

        volumeInput.oninput = () => {
            volumeInput.title = Number.parseInt(volumeInput.value * 100) + "%";
            volumeLabel.innerText = `Volume ${volumeInput.title}`;
            if (this.#stream) {
                this.#stream.setVolume(volumeInput.value);
            }
        }

        const volumeBlock = this.shadowRoot.getElementById("volume-block");
        volumeBlock.ondblclick = () => {
            volumeInput.title = "100%";
            volumeLabel.dataset.i18nValue = volumeInput.title;
            volumeInput.value = 1;
        }
    }
}

customElements.define("voice-context-menu", VoiceContextMenu);
customElements.define("stream-context-menu", StreamContextMenu);