class TrashIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 16 16">
                    <path clip-rule="evenodd"
                        d="M5 3.25V4H2.75a.75.75 0 0 0 0 1.5h.3l.815 8.15A1.5 1.5 0 0 0 5.357 15h5.285a1.5 1.5 0 0 0 1.493-1.35l.815-8.15h.3a.75.75 0 0 0 0-1.5H11v-.75A2.25 2.25 0 0 0 8.75 1h-1.5A2.25 2.25 0 0 0 5 3.25Zm2.25-.75a.75.75 0 0 0-.75.75V4h3v-.75a.75.75 0 0 0-.75-.75h-1.5ZM6.05 6a.75.75 0 0 1 .787.713l.275 5.5a.75.75 0 0 1-1.498.075l-.275-5.5A.75.75 0 0 1 6.05 6Zm3.9 0a.75.75 0 0 1 .712.787l-.275 5.5a.75.75 0 0 1-1.498-.075l.275-5.5a.75.75 0 0 1 .786-.711Z"
                        fill-rule="evenodd"></path>
            </svg>`
    }
}

class PencilIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 16 16">
                <path clip-rule="evenodd"
                    d="M11.013 2.513a1.75 1.75 0 0 1 2.475 2.474L6.226 12.25a2.751 2.751 0 0 1-.892.596l-2.047.848a.75.75 0 0 1-.98-.98l.848-2.047a2.75 2.75 0 0 1 .596-.892l7.262-7.261Z"
                    fill-rule="evenodd"></path>
            </svg>`
    }
}

class AnswerIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" fill="none" stroke-width="1.5" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 15 3 9m0 0 6-6M3 9h12a6 6 0 0 1 0 12h-3"></path>
            </svg>`
    }
}

class ChatBubbleIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24">
                <path clip-rule="evenodd"
                    d="M4.804 21.644A6.707 6.707 0 0 0 6 21.75a6.721 6.721 0 0 0 3.583-1.029c.774.182 1.584.279 2.417.279 5.322 0 9.75-3.97 9.75-9 0-5.03-4.428-9-9.75-9s-9.75 3.97-9.75 9c0 2.409 1.025 4.587 2.674 6.192.232.226.277.428.254.543a3.73 3.73 0 0 1-.814 1.686.75.75 0 0 0 .44 1.223ZM8.25 10.875a1.125 1.125 0 1 0 0 2.25 1.125 1.125 0 0 0 0-2.25ZM10.875 12a1.125 1.125 0 1 1 2.25 0 1.125 1.125 0 0 1-2.25 0Zm4.875-1.125a1.125 1.125 0 1 0 0 2.25 1.125 1.125 0 0 0 0-2.25Z"
                    fill-rule="evenodd"></path>
            </svg>`
    }
}

class PrivateDiscussionIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path d="M3.505 2.365A41.369 41.369 0 0 1 9 2c1.863 0 3.697.124 5.495.365 1.247.167 2.18 1.108 2.435 2.268a4.45 4.45 0 0 0-.577-.069 43.141 43.141 0 0 0-4.706 0C9.229 4.696 7.5 6.727 7.5 8.998v2.24c0 1.413.67 2.735 1.76 3.562l-2.98 2.98A.75.75 0 0 1 5 17.25v-3.443c-.501-.048-1-.106-1.495-.172C2.033 13.438 1 12.162 1 10.72V5.28c0-1.441 1.033-2.717 2.505-2.914Z"></path>
                <path d="M14 6c-.762 0-1.52.02-2.271.062C10.157 6.148 9 7.472 9 8.998v2.24c0 1.519 1.147 2.839 2.71 2.935.214.013.428.024.642.034.2.009.385.09.518.224l2.35 2.35a.75.75 0 0 0 1.28-.531v-2.07c1.453-.195 2.5-1.463 2.5-2.915V8.998c0-1.526-1.157-2.85-2.729-2.936A41.645 41.645 0 0 0 14 6Z"></path>
            </svg>`
    }
}

class PhoneIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24">
                <path clip-rule="evenodd"
                    d="M1.5 4.5a3 3 0 0 1 3-3h1.372c.86 0 1.61.586 1.819 1.42l1.105 4.423a1.875 1.875 0 0 1-.694 1.955l-1.293.97c-.135.101-.164.249-.126.352a11.285 11.285 0 0 0 6.697 6.697c.103.038.25.009.352-.126l.97-1.293a1.875 1.875 0 0 1 1.955-.694l4.423 1.105c.834.209 1.42.959 1.42 1.82V19.5a3 3 0 0 1-3 3h-2.25C8.552 22.5 1.5 15.448 1.5 6.75V4.5Z"
                    fill-rule="evenodd"></path>
            </svg>`
    }
}

class PhoneXIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24">
                <path clip-rule="evenodd"
                    d="M15.22 3.22a.75.75 0 0 1 1.06 0L18 4.94l1.72-1.72a.75.75 0 1 1 1.06 1.06L19.06 6l1.72 1.72a.75.75 0 0 1-1.06 1.06L18 7.06l-1.72 1.72a.75.75 0 1 1-1.06-1.06L16.94 6l-1.72-1.72a.75.75 0 0 1 0-1.06ZM1.5 4.5a3 3 0 0 1 3-3h1.372c.86 0 1.61.586 1.819 1.42l1.105 4.423a1.875 1.875 0 0 1-.694 1.955l-1.293.97c-.135.101-.164.249-.126.352a11.285 11.285 0 0 0 6.697 6.697c.103.038.25.009.352-.126l.97-1.293a1.875 1.875 0 0 1 1.955-.694l4.423 1.105c.834.209 1.42.959 1.42 1.82V19.5a3 3 0 0 1-3 3h-2.25C8.552 22.5 1.5 15.448 1.5 6.75V4.5Z"
                    fill-rule="evenodd"></path>
            </svg>`
    }
}

class MicrophoneIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24">
                <path d="M8.25 4.5a3.75 3.75 0 1 1 7.5 0v8.25a3.75 3.75 0 1 1-7.5 0V4.5Z"></path>
                <path d="M6 10.5a.75.75 0 0 1 .75.75v1.5a5.25 5.25 0 1 0 10.5 0v-1.5a.75.75 0 0 1 1.5 0v1.5a6.751 6.751 0 0 1-6 6.709v2.291h3a.75.75 0 0 1 0 1.5h-7.5a.75.75 0 0 1 0-1.5h3v-2.291a6.751 6.751 0 0 1-6-6.709v-1.5A.75.75 0 0 1 6 10.5Z"></path>
            </svg>`
    }
}

class ClipboardIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="none" stroke-width="1.5" stroke="currentColor" viewBox="0 0 24 24">
                <path d="M9 12h3.75M9 15h3.75M9 18h3.75m3 .75H18a2.25 2.25 0 0 0 2.25-2.25V6.108c0-1.135-.845-2.098-1.976-2.192a48.424 48.424 0 0 0-1.123-.08m-5.801 0c-.065.21-.1.433-.1.664 0 .414.336.75.75.75h4.5a.75.75 0 0 0 .75-.75 2.25 2.25 0 0 0-.1-.664m-5.8 0A2.251 2.251 0 0 1 13.5 2.25H15c1.012 0 1.867.668 2.15 1.586m-5.8 0c-.376.023-.75.05-1.124.08C9.095 4.01 8.25 4.973 8.25 6.108V8.25m0 0H4.875c-.621 0-1.125.504-1.125 1.125v11.25c0 .621.504 1.125 1.125 1.125h9.75c.621 0 1.125-.504 1.125-1.125V9.375c0-.621-.504-1.125-1.125-1.125H8.25ZM6.75 12h.008v.008H6.75V12Zm0 3h.008v.008H6.75V15Zm0 3h.008v.008H6.75V18Z"
                    stroke-linecap="round"
                    stroke-linejoin="round"></path>
            </svg>`
    }
}

class CirclePlusIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path clip-rule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25ZM12.75 9a.75.75 0 0 0-1.5 0v2.25H9a.75.75 0 0 0 0 1.5h2.25V15a.75.75 0 0 0 1.5 0v-2.25H15a.75.75 0 0 0 0-1.5h-2.25V9Z" fill-rule="evenodd"></path>
            </svg>`
    }
}

class FolderPlusIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path clip-rule="evenodd" d="M19.5 21a3 3 0 0 0 3-3V9a3 3 0 0 0-3-3h-5.379a.75.75 0 0 1-.53-.22L11.47 3.66A2.25 2.25 0 0 0 9.879 3H4.5a3 3 0 0 0-3 3v12a3 3 0 0 0 3 3h15Zm-6.75-10.5a.75.75 0 0 0-1.5 0v2.25H9a.75.75 0 0 0 0 1.5h2.25v2.25a.75.75 0 0 0 1.5 0v-2.25H15a.75.75 0 0 0 0-1.5h-2.25V10.5Z" fill-rule="evenodd"></path>
            </svg>`
    }
}

class EyeOpenIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z"></path>
                <path clip-rule="evenodd" d="M1.323 11.447C2.811 6.976 7.028 3.75 12.001 3.75c4.97 0 9.185 3.223 10.675 7.69.12.362.12.752 0 1.113-1.487 4.471-5.705 7.697-10.677 7.697-4.97 0-9.186-3.223-10.675-7.69a1.762 1.762 0 0 1 0-1.113ZM17.25 12a5.25 5.25 0 1 1-10.5 0 5.25 5.25 0 0 1 10.5 0Z" fill-rule="evenodd"></path>
            </svg>`
    }
}

class FolderIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M19.5 21a3 3 0 0 0 3-3v-4.5a3 3 0 0 0-3-3h-15a3 3 0 0 0-3 3V18a3 3 0 0 0 3 3h15ZM1.5 10.146V6a3 3 0 0 1 3-3h5.379a2.25 2.25 0 0 1 1.59.659l2.122 2.121c.14.141.331.22.53.22H19.5a3 3 0 0 1 3 3v1.146A4.483 4.483 0 0 0 19.5 9h-15a4.483 4.483 0 0 0-3 1.146Z"></path>
            </svg>`
    }
}

class Cog6ToothIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path clip-rule="evenodd" d="M11.078 2.25c-.917 0-1.699.663-1.85 1.567L9.05 4.889c-.02.12-.115.26-.297.348a7.493 7.493 0 0 0-.986.57c-.166.115-.334.126-.45.083L6.3 5.508a1.875 1.875 0 0 0-2.282.819l-.922 1.597a1.875 1.875 0 0 0 .432 2.385l.84.692c.095.078.17.229.154.43a7.598 7.598 0 0 0 0 1.139c.015.2-.059.352-.153.43l-.841.692a1.875 1.875 0 0 0-.432 2.385l.922 1.597a1.875 1.875 0 0 0 2.282.818l1.019-.382c.115-.043.283-.031.45.082.312.214.641.405.985.57.182.088.277.228.297.35l.178 1.071c.151.904.933 1.567 1.85 1.567h1.844c.916 0 1.699-.663 1.85-1.567l.178-1.072c.02-.12.114-.26.297-.349.344-.165.673-.356.985-.57.167-.114.335-.125.45-.082l1.02.382a1.875 1.875 0 0 0 2.28-.819l.923-1.597a1.875 1.875 0 0 0-.432-2.385l-.84-.692c-.095-.078-.17-.229-.154-.43a7.614 7.614 0 0 0 0-1.139c-.016-.2.059-.352.153-.43l.84-.692c.708-.582.891-1.59.433-2.385l-.922-1.597a1.875 1.875 0 0 0-2.282-.818l-1.02.382c-.114.043-.282.031-.449-.083a7.49 7.49 0 0 0-.985-.57c-.183-.087-.277-.227-.297-.348l-.179-1.072a1.875 1.875 0 0 0-1.85-1.567h-1.843ZM12 15.75a3.75 3.75 0 1 0 0-7.5 3.75 3.75 0 0 0 0 7.5Z" fill-rule="evenodd"></path>
            </svg>`
    }
}

class UsersIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M4.5 6.375a4.125 4.125 0 1 1 8.25 0 4.125 4.125 0 0 1-8.25 0ZM14.25 8.625a3.375 3.375 0 1 1 6.75 0 3.375 3.375 0 0 1-6.75 0ZM1.5 19.125a7.125 7.125 0 0 1 14.25 0v.003l-.001.119a.75.75 0 0 1-.363.63 13.067 13.067 0 0 1-6.761 1.873c-2.472 0-4.786-.684-6.76-1.873a.75.75 0 0 1-.364-.63l-.001-.122ZM17.25 19.128l-.001.144a2.25 2.25 0 0 1-.233.96 10.088 10.088 0 0 0 5.06-1.01.75.75 0 0 0 .42-.643 4.875 4.875 0 0 0-6.957-4.611 8.586 8.586 0 0 1 1.71 5.157v.003Z"></path>
            </svg>`
    }
}

class InformationIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path clip-rule="evenodd" d="M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12Zm8.706-1.442c1.146-.573 2.437.463 2.126 1.706l-.709 2.836.042-.02a.75.75 0 0 1 .67 1.34l-.04.022c-1.147.573-2.438-.463-2.127-1.706l.71-2.836-.042.02a.75.75 0 1 1-.671-1.34l.041-.022ZM12 9a.75.75 0 1 0 0-1.5.75.75 0 0 0 0 1.5Z" fill-rule="evenodd"></path>
            </svg>`
    }
}

class SwatchIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path clip-rule="evenodd" d="M2.25 4.125c0-1.036.84-1.875 1.875-1.875h5.25c1.036 0 1.875.84 1.875 1.875V17.25a4.5 4.5 0 1 1-9 0V4.125Zm4.5 14.25a1.125 1.125 0 1 0 0-2.25 1.125 1.125 0 0 0 0 2.25Z" fill-rule="evenodd"></path>
                <path d="M10.719 21.75h9.156c1.036 0 1.875-.84 1.875-1.875v-5.25c0-1.036-.84-1.875-1.875-1.875h-.14l-8.742 8.743c-.09.089-.18.175-.274.257ZM12.738 17.625l6.474-6.474a1.875 1.875 0 0 0 0-2.651L15.5 4.787a1.875 1.875 0 0 0-2.651 0l-.1.099V17.25c0 .126-.003.251-.01.375Z"></path>
            </svg>`
    }
}

class CircleXIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path clip-rule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25Zm-1.72 6.97a.75.75 0 1 0-1.06 1.06L10.94 12l-1.72 1.72a.75.75 0 1 0 1.06 1.06L12 13.06l1.72 1.72a.75.75 0 1 0 1.06-1.06L13.06 12l1.72-1.72a.75.75 0 1 0-1.06-1.06L12 10.94l-1.72-1.72Z" fill-rule="evenodd"></path>
            </svg>`
    }
}

class CircleCheckIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-6">
                <path fill-rule="evenodd" d="M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12Zm13.36-1.814a.75.75 0 1 0-1.22-.872l-3.236 4.53L9.53 12.22a.75.75 0 0 0-1.06 1.06l2.25 2.25a.75.75 0 0 0 1.14-.094l3.75-5.25Z" clip-rule="evenodd" />
            </svg>`
    }
}

class EnvelopeIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M1.5 8.67v8.58a3 3 0 0 0 3 3h15a3 3 0 0 0 3-3V8.67l-8.928 5.493a3 3 0 0 1-3.144 0L1.5 8.67Z"></path>
                <path d="M22.5 6.908V6.75a3 3 0 0 0-3-3h-15a3 3 0 0 0-3 3v.158l9.714 5.978a1.5 1.5 0 0 0 1.572 0L22.5 6.908Z"></path>
            </svg>`
    }
}

class ArrowPointingIn extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path clip-rule="evenodd" d="M3.22 3.22a.75.75 0 0 1 1.06 0l3.97 3.97V4.5a.75.75 0 0 1 1.5 0V9a.75.75 0 0 1-.75.75H4.5a.75.75 0 0 1 0-1.5h2.69L3.22 4.28a.75.75 0 0 1 0-1.06Zm17.56 0a.75.75 0 0 1 0 1.06l-3.97 3.97h2.69a.75.75 0 0 1 0 1.5H15a.75.75 0 0 1-.75-.75V4.5a.75.75 0 0 1 1.5 0v2.69l3.97-3.97a.75.75 0 0 1 1.06 0ZM3.75 15a.75.75 0 0 1 .75-.75H9a.75.75 0 0 1 .75.75v4.5a.75.75 0 0 1-1.5 0v-2.69l-3.97 3.97a.75.75 0 0 1-1.06-1.06l3.97-3.97H4.5a.75.75 0 0 1-.75-.75Zm10.5 0a.75.75 0 0 1 .75-.75h4.5a.75.75 0 0 1 0 1.5h-2.69l3.97 3.97a.75.75 0 1 1-1.06 1.06l-3.97-3.97v2.69a.75.75 0 0 1-1.5 0V15Z" fill-rule="evenodd"></path>
            </svg>`
    }
}

class SpeakerIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M13.5 4.06c0-1.336-1.616-2.005-2.56-1.06l-4.5 4.5H4.508c-1.141 0-2.318.664-2.66 1.905A9.76 9.76 0 0 0 1.5 12c0 .898.121 1.768.35 2.595.341 1.24 1.518 1.905 2.659 1.905h1.93l4.5 4.5c.945.945 2.561.276 2.561-1.06V4.06ZM18.584 5.106a.75.75 0 0 1 1.06 0c3.808 3.807 3.808 9.98 0 13.788a.75.75 0 0 1-1.06-1.06 8.25 8.25 0 0 0 0-11.668.75.75 0 0 1 0-1.06Z"></path>
                <path d="M15.932 7.757a.75.75 0 0 1 1.061 0 6 6 0 0 1 0 8.486.75.75 0 0 1-1.06-1.061 4.5 4.5 0 0 0 0-6.364.75.75 0 0 1 0-1.06Z"></path>
            </svg>`
    }
}

class SpeakerXIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M13.5 4.06c0-1.336-1.616-2.005-2.56-1.06l-4.5 4.5H4.508c-1.141 0-2.318.664-2.66 1.905A9.76 9.76 0 0 0 1.5 12c0 .898.121 1.768.35 2.595.341 1.24 1.518 1.905 2.659 1.905h1.93l4.5 4.5c.945.945 2.561.276 2.561-1.06V4.06ZM17.78 9.22a.75.75 0 1 0-1.06 1.06L18.44 12l-1.72 1.72a.75.75 0 1 0 1.06 1.06l1.72-1.72 1.72 1.72a.75.75 0 1 0 1.06-1.06L20.56 12l1.72-1.72a.75.75 0 1 0-1.06-1.06l-1.72 1.72-1.72-1.72Z"></path>
            </svg>`
    }
}

class EmojiIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path clip-rule="evenodd" fill-rule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25Zm-2.625 6c-.54 0-.828.419-.936.634a1.96 1.96 0 0 0-.189.866c0 .298.059.605.189.866.108.215.395.634.936.634.54 0 .828-.419.936-.634.13-.26.189-.568.189-.866 0-.298-.059-.605-.189-.866-.108-.215-.395-.634-.936-.634Zm4.314.634c.108-.215.395-.634.936-.634.54 0 .828.419.936.634.13.26.189.568.189.866 0 .298-.059.605-.189.866-.108.215-.395.634-.936.634-.54 0-.828-.419-.936-.634a1.96 1.96 0 0 1-.189-.866c0-.298.059-.605.189-.866Zm2.023 6.828a.75.75 0 1 0-1.06-1.06 3.75 3.75 0 0 1-5.304 0 .75.75 0 0 0-1.06 1.06 5.25 5.25 0 0 0 7.424 0Z"></path>
            </svg>`
    }
}

class UserIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path clip-rule="evenodd" d="M7.5 6a4.5 4.5 0 1 1 9 0 4.5 4.5 0 0 1-9 0ZM3.751 20.105a8.25 8.25 0 0 1 16.498 0 .75.75 0 0 1-.437.695A18.683 18.683 0 0 1 12 22.5c-2.786 0-5.433-.608-7.812-1.7a.75.75 0 0 1-.437-.695Z" fill-rule="evenodd"></path>
            </svg>`
    }
}

class RoleIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
              <defs>
                <mask id="userMask">
                  <rect width="24" height="24" fill="white"/>
                  <g transform="translate(12, 12) scale(0.55) translate(-12, -12)">
                    <path clip-rule="evenodd"
                          fill-rule="evenodd"
                          fill="black"
                          d="M7.5 6a4.5 4.5 0 1 1 9 0 4.5 4.5 0 0 1-9 0ZM3.751 20.105a8.25 8.25 0 0 1 16.498 0 .75.75 0 0 1-.437.695A18.683 18.683 0 0 1 12 22.5c-2.786 0-5.433-.608-7.812-1.7a.75.75 0 0 1-.437-.695Z">
                    </path>
                  </g>
                </mask>
              </defs>
              <path clip-rule="evenodd"
                    fill-rule="evenodd"
                    mask="url(#userMask)"
                    d="M12.516 2.17a.75.75 0 0 0-1.032 0 11.209 11.209 0 0 1-7.877 3.08.75.75 0 0 0-.722.515A12.74 12.74 0 0 0 2.25 9.75c0 5.942 4.064 10.933 9.563 12.348a.749.749 0 0 0 .374 0c5.499-1.415 9.563-6.406 9.563-12.348 0-1.39-.223-2.73-.635-3.985a.75.75 0 0 0-.722-.516l-.143.001c-2.996 0-5.717-1.17-7.734-3.08Z">
              </path>
            </svg>`
    }
}
class ModerationIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <defs>
                    <mask id="gavelMask">
                        <rect width="24" height="24" fill="white"/>
                        <g transform="translate(6.5, 6.5) scale(0.023)">
                            <path fill="black" d="M69.684,384.436l116.312-116.312c11.096-11.096,14.282-26.938,9.933-40.952l36.01-36.01
                        c10.762,8.763,21.923,15.769,32.896,20.882c0.837-2.495,2.13-4.788,4.016-6.674l60.306-60.306c1.87-1.87,4.178-3.138,6.706-3.942
                        c-7.381-15.867-18.696-32.173-33.555-47.032c-15.152-15.152-31.807-26.654-47.975-34.018c-0.845,2.341-2.065,4.511-3.853,6.308
                        l-60.306,60.306c-1.78,1.78-3.902,3.081-6.267,3.95c5.145,11.266,12.331,22.752,21.338,33.823l-36.01,36.01
                        c-14.014-4.349-29.856-1.162-40.952,9.933L11.955,326.715c-15.94,15.94-15.94,41.781,0,57.721
                        C27.895,400.376,53.744,400.376,69.684,384.436z"/>
                            <path fill="black" d="M274.589,211.127c-1.122,1.122-1.837,2.544-2.276,4.121c-1.496,5.414,0.91,12.9,6.576,18.566
                        c4.438,4.43,10.128,7.08,15.233,7.08c2.138,0,5.162-0.48,7.454-2.772l18.647-18.647l23.012-23.012l18.647-18.647
                        c2.284-2.284,3.211-5.779,2.593-9.836c-0.683-4.519-3.138-9.08-6.901-12.843c-4.438-4.43-10.128-7.08-15.225-7.08
                        c-0.984,0-2.162,0.13-3.365,0.463c-1.414,0.39-2.861,1.073-4.097,2.317L274.589,211.127z"/>
                            <path fill="black" d="M176.949,123.728c1.105,0,2.455-0.146,3.82-0.593c1.26-0.415,2.536-1.081,3.642-2.187l60.306-60.306
                        c1.008-1.008,1.658-2.284,2.113-3.666c1.772-5.438-0.585-13.185-6.422-19.013c-4.438-4.43-10.128-7.08-15.225-7.08
                        c-2.138,0-5.162,0.48-7.454,2.772l-20.549,20.549l-19.216,19.208l-20.549,20.549c-4.975,4.983-3.008,15.371,4.308,22.687
                        C166.162,121.078,171.852,123.728,176.949,123.728z"/>
                            <path fill="black" d="M300.438,387.313c9.892-0.975,20.5-1.252,31.579-1.252c4.723,0,9.559,0.033,14.485,0.073
                        c10.209,0.081,21.004,0.081,31.214,0c4.926-0.041,9.762-0.073,14.485-0.073c11.079,0,21.687,0.276,31.579,1.252v-23.589H300.438V387.313z"/>
                            <path fill="black" d="M392.202,394.19c-4.698,0-9.51,0.033-14.42,0.073c-10.258,0.073-21.086,0.073-31.336,0
                        c-4.91-0.041-9.722-0.073-14.42-0.073c-11.502,0-22.053,0.228-31.579,1.13c-31.417,2.967-51.129,13.567-52.47,50.04h228.267
                        c-1.341-36.473-21.053-47.064-52.47-50.039C414.255,394.418,403.695,394.19,392.202,394.19z"/>
                        </g>
                    </mask>
                </defs>
                <path clip-rule="evenodd"
                      fill-rule="evenodd"
                      mask="url(#gavelMask)"
                      d="M12.516 2.17a.75.75 0 0 0-1.032 0 11.209 11.209 0 0 1-7.877 3.08.75.75 0 0 0-.722.515A12.74 12.74 0 0 0 2.25 9.75c0 5.942 4.064 10.933 9.563 12.348a.749.749 0 0 0 .374 0c5.499-1.415 9.563-6.406 9.563-12.348 0-1.39-.223-2.73-.635-3.985a.75.75 0 0 0-.722-.516l-.143.001c-2.996 0-5.717-1.17-7.734-3.08Z"/>
            </svg>`
    }
}

class ModerationHammerIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg height="800px" width="800px" fill="currentColor" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 476.243 476.243" xml:space="preserve">
                <g>
                    <path d="M69.684,384.436l116.312-116.312c11.096-11.096,14.282-26.938,9.933-40.952l36.01-36.01
                        c10.762,8.763,21.923,15.769,32.896,20.882c0.837-2.495,2.13-4.788,4.016-6.674l60.306-60.306c1.87-1.87,4.178-3.138,6.706-3.942
                        c-7.381-15.867-18.696-32.173-33.555-47.032c-15.152-15.152-31.807-26.654-47.975-34.018c-0.845,2.341-2.065,4.511-3.853,6.308
                        l-60.306,60.306c-1.78,1.78-3.902,3.081-6.267,3.95c5.145,11.266,12.331,22.752,21.338,33.823l-36.01,36.01
                        c-14.014-4.349-29.856-1.162-40.952,9.933L11.955,326.715c-15.94,15.94-15.94,41.781,0,57.721
                        C27.895,400.376,53.744,400.376,69.684,384.436z"/>
                    <path d="M274.589,211.127c-1.122,1.122-1.837,2.544-2.276,4.121c-1.496,5.414,0.91,12.9,6.576,18.566
                        c4.438,4.43,10.128,7.08,15.233,7.08c2.138,0,5.162-0.48,7.454-2.772l18.647-18.647l23.012-23.012l18.647-18.647
                        c2.284-2.284,3.211-5.779,2.593-9.836c-0.683-4.519-3.138-9.08-6.901-12.843c-4.438-4.43-10.128-7.08-15.225-7.08
                        c-0.984,0-2.162,0.13-3.365,0.463c-1.414,0.39-2.861,1.073-4.097,2.317L274.589,211.127z"/>
                    <path d="M176.949,123.728c1.105,0,2.455-0.146,3.82-0.593c1.26-0.415,2.536-1.081,3.642-2.187l60.306-60.306
                        c1.008-1.008,1.658-2.284,2.113-3.666c1.772-5.438-0.585-13.185-6.422-19.013c-4.438-4.43-10.128-7.08-15.225-7.08
                        c-2.138,0-5.162,0.48-7.454,2.772l-20.549,20.549l-19.216,19.208l-20.549,20.549c-4.975,4.983-3.008,15.371,4.308,22.687
                        C166.162,121.078,171.852,123.728,176.949,123.728z"/>
                    <path d="M300.438,387.313c9.892-0.975,20.5-1.252,31.579-1.252c4.723,0,9.559,0.033,14.485,0.073
                        c10.209,0.081,21.004,0.081,31.214,0c4.926-0.041,9.762-0.073,14.485-0.073c11.079,0,21.687,0.276,31.579,1.252v-23.589H300.438
                        V387.313z"/>
                    <path d="M392.202,394.19c-4.698,0-9.51,0.033-14.42,0.073c-10.258,0.073-21.086,0.073-31.336,0
                        c-4.91-0.041-9.722-0.073-14.42-0.073c-11.502,0-22.053,0.228-31.579,1.13c-31.417,2.967-51.129,13.567-52.47,50.04h228.267
                        c-1.341-36.473-21.053-47.064-52.47-50.039C414.255,394.418,403.695,394.19,392.202,394.19z"/>
                </g>
            </svg>`
    }
}


class PaperClipIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="none" stroke-width="1.5" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="m18.375 12.739-7.693 7.693a4.5 4.5 0 0 1-6.364-6.364l10.94-10.94A3 3 0 1 1 19.5 7.372L8.552 18.32m.009-.01-.01.01m5.699-9.941-7.81 7.81a1.5 1.5 0 0 0 2.112 2.13" stroke-linecap="round" stroke-linejoin="round"></path>
            </svg>`
    }
}

class LogoutIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="none" stroke-width="1.5" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M15.75 9V5.25A2.25 2.25 0 0 0 13.5 3h-6a2.25 2.25 0 0 0-2.25 2.25v13.5A2.25 2.25 0 0 0 7.5 21h6a2.25 2.25 0 0 0 2.25-2.25V15m3 0 3-3m0 0-3-3m3 3H9" stroke-linecap="round" stroke-linejoin="round"></path>
            </svg>`
    }
}

class MenuBurgerIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-6">
                <path fill-rule="evenodd" d="M3 5.25a.75.75 0 0 1 .75-.75h16.5a.75.75 0 0 1 0 1.5H3.75A.75.75 0 0 1 3 5.25Zm0 4.5A.75.75 0 0 1 3.75 9h16.5a.75.75 0 0 1 0 1.5H3.75A.75.75 0 0 1 3 9.75Zm0 4.5a.75.75 0 0 1 .75-.75h16.5a.75.75 0 0 1 0 1.5H3.75a.75.75 0 0 1-.75-.75Zm0 4.5a.75.75 0 0 1 .75-.75h16.5a.75.75 0 0 1 0 1.5H3.75a.75.75 0 0 1-.75-.75Z" clip-rule="evenodd" />
            </svg>`
    }
}

class CameraIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M4.5 4.5a3 3 0 0 0-3 3v9a3 3 0 0 0 3 3h8.25a3 3 0 0 0 3-3v-9a3 3 0 0 0-3-3H4.5ZM19.94 18.75l-2.69-2.69V7.94l2.69-2.69c.944-.945 2.56-.276 2.56 1.06v11.38c0 1.336-1.616 2.005-2.56 1.06Z"></path>
            </svg>`
    }
}

class DisplayIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path clip-rule="evenodd" d="M2.25 6a3 3 0 0 1 3-3h13.5a3 3 0 0 1 3 3v12a3 3 0 0 1-3 3H5.25a3 3 0 0 1-3-3V6Zm18 3H3.75v9a1.5 1.5 0 0 0 1.5 1.5h13.5a1.5 1.5 0 0 0 1.5-1.5V9Zm-15-3.75A.75.75 0 0 0 4.5 6v.008c0 .414.336.75.75.75h.008a.75.75 0 0 0 .75-.75V6a.75.75 0 0 0-.75-.75H5.25Zm1.5.75a.75.75 0 0 1 .75-.75h.008a.75.75 0 0 1 .75.75v.008a.75.75 0 0 1-.75.75H7.5a.75.75 0 0 1-.75-.75V6Zm3-.75A.75.75 0 0 0 9 6v.008c0 .414.336.75.75.75h.008a.75.75 0 0 0 .75-.75V6a.75.75 0 0 0-.75-.75H9.75Z" fill-rule="evenodd"></path>
            </svg>`
    }
}

class LanguageIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" fill="none" stroke-width="1.5" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" d="m10.5 21 5.25-11.25L21 21m-9-3h7.5M3 5.621a48.474 48.474 0 0 1 6-.371m0 0c1.12 0 2.233.038 3.334.114M9 5.25V3m3.334 2.364C11.176 10.658 7.69 15.08 3 17.502m9.334-12.138c.896.061 1.785.147 2.666.257m-4.589 8.495a18.023 18.023 0 0 1-3.827-5.802"></path>
            </svg>`
    }
}

class AppearanceIconComponent extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-6">
                <path d="M11.25 5.337c0-.355-.186-.676-.401-.959a1.647 1.647 0 0 1-.349-1.003c0-1.036 1.007-1.875 2.25-1.875S15 2.34 15 3.375c0 .369-.128.713-.349 1.003-.215.283-.401.604-.401.959 0 .332.278.598.61.578 1.91-.114 3.79-.342 5.632-.676a.75.75 0 0 1 .878.645 49.17 49.17 0 0 1 .376 5.452.657.657 0 0 1-.66.664c-.354 0-.675-.186-.958-.401a1.647 1.647 0 0 0-1.003-.349c-1.035 0-1.875 1.007-1.875 2.25s.84 2.25 1.875 2.25c.369 0 .713-.128 1.003-.349.283-.215.604-.401.959-.401.31 0 .557.262.534.571a48.774 48.774 0 0 1-.595 4.845.75.75 0 0 1-.61.61c-1.82.317-3.673.533-5.555.642a.58.58 0 0 1-.611-.581c0-.355.186-.676.401-.959.221-.29.349-.634.349-1.003 0-1.035-1.007-1.875-2.25-1.875s-2.25.84-2.25 1.875c0 .369.128.713.349 1.003.215.283.401.604.401.959a.641.641 0 0 1-.658.643 49.118 49.118 0 0 1-4.708-.36.75.75 0 0 1-.645-.878c.293-1.614.504-3.257.629-4.924A.53.53 0 0 0 5.337 15c-.355 0-.676.186-.959.401-.29.221-.634.349-1.003.349-1.036 0-1.875-1.007-1.875-2.25s.84-2.25 1.875-2.25c.369 0 .713.128 1.003.349.283.215.604.401.959.401a.656.656 0 0 0 .659-.663 47.703 47.703 0 0 0-.31-4.82.75.75 0 0 1 .83-.832c1.343.155 2.703.254 4.077.294a.64.64 0 0 0 .657-.642Z" />
            </svg>`
    }
}

class ArrowPointingOut extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
            <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path clip-rule="evenodd" d="M16.5 3.75a1.5 1.5 0 0 1 1.5 1.5v13.5a1.5 1.5 0 0 1-1.5 1.5h-6a1.5 1.5 0 0 1-1.5-1.5V15a.75.75 0 0 0-1.5 0v3.75a3 3 0 0 0 3 3h6a3 3 0 0 0 3-3V5.25a3 3 0 0 0-3-3h-6a3 3 0 0 0-3 3V9A.75.75 0 1 0 9 9V5.25a1.5 1.5 0 0 1 1.5-1.5h6ZM5.78 8.47a.75.75 0 0 0-1.06 0l-3 3a.75.75 0 0 0 0 1.06l3 3a.75.75 0 0 0 1.06-1.06l-1.72-1.72H15a.75.75 0 0 0 0-1.5H4.06l1.72-1.72a.75.75 0 0 0 0-1.06Z" fill-rule="evenodd"></path>
            </svg>`
    }
}

class Telescope extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-telescope-icon lucide-telescope"><path d="m10.065 12.493-6.18 1.318a.934.934 0 0 1-1.108-.702l-.537-2.15a1.07 1.07 0 0 1 .691-1.265l13.504-4.44"/><path d="m13.56 11.747 4.332-.924"/><path d="m16 21-3.105-6.21"/><path d="M16.485 5.94a2 2 0 0 1 1.455-2.425l1.09-.272a1 1 0 0 1 1.212.727l1.515 6.06a1 1 0 0 1-.727 1.213l-1.09.272a2 2 0 0 1-2.425-1.455z"/><path d="m6.158 8.633 1.114 4.456"/><path d="m8 21 3.105-6.21"/><circle cx="12" cy="13" r="2"/></svg>`
    }
}

class SquarePlus extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path d="M6 3a3 3 0 0 0-3 3v2.25a3 3 0 0 0 3 3h2.25a3 3 0 0 0 3-3V6a3 3 0 0 0-3-3H6ZM15.75 3a3 3 0 0 0-3 3v2.25a3 3 0 0 0 3 3H18a3 3 0 0 0 3-3V6a3 3 0 0 0-3-3h-2.25ZM6 12.75a3 3 0 0 0-3 3V18a3 3 0 0 0 3 3h2.25a3 3 0 0 0 3-3v-2.25a3 3 0 0 0-3-3H6ZM17.625 13.5a.75.75 0 0 0-1.5 0v2.625H13.5a.75.75 0 0 0 0 1.5h2.625v2.625a.75.75 0 0 0 1.5 0v-2.625h2.625a.75.75 0 0 0 0-1.5h-2.625V13.5Z"></path>
        </svg>`
    }
}

class Wrench extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path clip-rule="evenodd" d="M12 6.75a5.25 5.25 0 0 1 6.775-5.025.75.75 0 0 1 .313 1.248l-3.32 3.319c.063.475.276.934.641 1.299.365.365.824.578 1.3.64l3.318-3.319a.75.75 0 0 1 1.248.313 5.25 5.25 0 0 1-5.472 6.756c-1.018-.086-1.87.1-2.309.634L7.344 21.3A3.298 3.298 0 1 1 2.7 16.657l8.684-7.151c.533-.44.72-1.291.634-2.309A5.342 5.342 0 0 1 12 6.75ZM4.117 19.125a.75.75 0 0 1 .75-.75h.008a.75.75 0 0 1 .75.75v.008a.75.75 0 0 1-.75.75h-.008a.75.75 0 0 1-.75-.75v-.008Z" fill-rule="evenodd"></path>
        </svg>`
    }
}

class ServerStack extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <svg data-slot="icon" aria-hidden="true" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path d="M5.507 4.048A3 3 0 0 1 7.785 3h8.43a3 3 0 0 1 2.278 1.048l1.722 2.008A4.533 4.533 0 0 0 19.5 6h-15c-.243 0-.482.02-.715.056l1.722-2.008Z"></path>
            <path clip-rule="evenodd" d="M1.5 10.5a3 3 0 0 1 3-3h15a3 3 0 1 1 0 6h-15a3 3 0 0 1-3-3Zm15 0a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Zm2.25.75a.75.75 0 1 0 0-1.5.75.75 0 0 0 0 1.5ZM4.5 15a3 3 0 1 0 0 6h15a3 3 0 1 0 0-6h-15Zm11.25 3.75a.75.75 0 1 0 0-1.5.75.75 0 0 0 0 1.5ZM19.5 18a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Z" fill-rule="evenodd"></path>
        </svg>`
    }
}

export const icons = {
    'revoice-icon-pencil': PencilIconComponent,
    'revoice-icon-trash': TrashIconComponent,
    'revoice-icon-answer': AnswerIconComponent,
    'revoice-icon-chat-bubble': ChatBubbleIconComponent,
    'revoice-icon-private-discussion': PrivateDiscussionIconComponent,
    'revoice-icon-phone': PhoneIconComponent,
    'revoice-icon-phone-x': PhoneXIconComponent,
    'revoice-icon-microphone': MicrophoneIconComponent,
    'revoice-icon-clipboard': ClipboardIconComponent,
    'revoice-icon-circle-plus': CirclePlusIconComponent,
    'revoice-icon-folder-plus': FolderPlusIconComponent,
    'revoice-icon-eye-open': EyeOpenIconComponent,
    'revoice-icon-folder': FolderIconComponent,
    'revoice-icon-cog-6': Cog6ToothIconComponent,
    'revoice-icon-users': UsersIconComponent,
    'revoice-icon-information': InformationIconComponent,
    'revoice-icon-swatch': SwatchIconComponent,
    'revoice-icon-circle-x': CircleXIconComponent,
    'revoice-icon-circle-check': CircleCheckIconComponent,
    'revoice-icon-envelope': EnvelopeIconComponent,
    'revoice-icon-arrow-in': ArrowPointingIn,
    'revoice-icon-speaker': SpeakerIconComponent,
    'revoice-icon-speaker-x': SpeakerXIconComponent,
    'revoice-icon-emoji': EmojiIconComponent,
    'revoice-icon-user': UserIconComponent,
    'revoice-icon-role': RoleIconComponent,
    'revoice-icon-moderation': ModerationIconComponent,
    'revoice-icon-moderation-hammer': ModerationHammerIconComponent,
    'revoice-icon-paper-clip': PaperClipIconComponent,
    'revoice-icon-logout': LogoutIconComponent,
    'revoice-icon-menu-burger': MenuBurgerIconComponent,
    'revoice-icon-camera': CameraIconComponent,
    'revoice-icon-display': DisplayIconComponent,
    'revoice-icon-language': LanguageIconComponent,
    'revoice-icon-appearance': AppearanceIconComponent,
    'revoice-icon-arrow-out': ArrowPointingOut,
    'revoice-icon-telescope': Telescope,
    'revoice-icon-square-plus': SquarePlus,
    'revoice-icon-wrench': Wrench,
    'revoice-icon-server-stack': ServerStack,
}

for (let iconsKey in icons) {
    customElements.define(iconsKey, icons[iconsKey]);
}