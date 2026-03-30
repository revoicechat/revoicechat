export default class Alert {
    static #defaultSounds = {
        messageNew: 'src/audio/messageNew.ogg',
        voiceUserJoin: 'src/audio/userJoinMale.mp3',
        voiceUserLeft: 'src/audio/userLeftMale.mp3',
        voiceConnected: 'src/audio/userConnectedMale.mp3',
        voiceDisconnected: 'src/audio/userDisconnectedMale.mp3',
        microphoneMuted: 'src/audio/microphoneMutedMale.mp3',
        microphoneActivated: 'src/audio/microphoneActivatedMale.mp3',
        soundMuted: 'src/audio/soundMutedMale.mp3',
        soundActivated: 'src/audio/soundActivatedMale.mp3',
    }
    static #testSounds = {
        notification: 'src/audio/tryNotificationMale.mp3',
        voiceChat: 'src/audio/tryVoiceChatMale.mp3',
    }

    static attachEvents(){
        document.getElementById("audio-output-try-voicechat").addEventListener('click', () => Alert.#playVoiceChat());
        document.getElementById("audio-output-try-notification").addEventListener('click', () => Alert.#playNotification());
    }

    /** @type {string} type */
    static play(type) {
        if (!this.#defaultSounds[type]) {
            console.error('Notification type is null or undefined');
        }

        let audio = new Audio(this.#defaultSounds[type]);
        audio.volume = RVC.userSettings().getNotificationVolume();
        audio.play();
    }

    static #playVoiceChat() {
        let audio = new Audio(this.#testSounds['voiceChat']);
        audio.volume = RVC.userSettings().getVoiceVolume();
        audio.play();
    }

    static #playNotification() {
        let audio = new Audio(this.#testSounds['notification']);
        audio.volume = RVC.userSettings().getNotificationVolume();
        audio.play();
    }
}