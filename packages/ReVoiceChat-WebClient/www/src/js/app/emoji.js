import {EmojiPicker, initCustomGeneral, initCustomServer, initCustomUser} from "../component/emoji.component.js";

const emojiPicker = new EmojiPicker();
emojiPicker.init().then(async () => {
    await initPicker()
    const pickerContainer = document.getElementById('emoji-picker');

    function addEmojiPickerButton(elementId) {
        const element = document.getElementById(elementId)
        element.addEventListener('click', (e) => {
            e.stopPropagation();
            emojiPicker.onEmojiSelect = handlePickerForMessage;
            emojiPicker.show(e.clientX, e.clientY);
        });
        document.addEventListener('click', (e) => {
            if (!pickerContainer.contains(e.target) && e.target !== element) {
                emojiPicker.hide();
            }
        });
    }

    addEmojiPickerButton('emoji-picker-button');
    addEmojiPickerButton('private-emoji-picker-button');
})

async function reloadEmojis() {
    await emojiPicker.init()
    await initPicker()
}

async function initPicker() {
    await initCustomUser(emojiPicker)
    await initCustomServer(emojiPicker)
    await initCustomGeneral(emojiPicker)
    const pickerContainer = document.getElementById('emoji-picker');
    pickerContainer.querySelector('#emoji-picker-content')?.remove();
    pickerContainer.appendChild(emojiPicker.create());
}

function handlePickerForMessage(emoji) {
    const messageInput = document.getElementById('text-input');
    const cursorPos = messageInput.selectionStart;
    const textBefore = messageInput.value.substring(0, cursorPos);
    const textAfter = messageInput.value.substring(cursorPos);
    const emojiText = emoji.dataset.emoji

    messageInput.value = textBefore + emojiText + textAfter;
    messageInput.focus();
    messageInput.selectionStart = messageInput.selectionEnd = cursorPos + emojiText.length;
}


export {reloadEmojis, emojiPicker}