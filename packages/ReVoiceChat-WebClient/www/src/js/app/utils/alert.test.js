import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import Alert from './Alert';

// Mock the RVC global
globalThis.RVC = {
  userSettings: function() {
    return {
      getNotificationVolume: function() { return 0.5; },
      getVoiceVolume: function() { return 0.7; },
    };
  },
};

// Mock Audio API
globalThis.Audio = class {
    volume = 1;
    play = vi.fn().mockResolvedValue(undefined);

    constructor(src) {
        this.src = src;
    }
};

describe('Alert', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // Mock DOM elements
    document.body.innerHTML = `
      <button id="audio-output-try-voicechat"></button>
      <button id="audio-output-try-notification"></button>
    `;
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  describe('attachEvents', () => {
    it('should attach click event to voicechat button', () => {
      const voiceChatBtn = document.getElementById('audio-output-try-voicechat');
      const addEventListenerSpy = vi.spyOn(voiceChatBtn, 'addEventListener');

      Alert.attachEvents();

      expect(addEventListenerSpy).toHaveBeenCalledWith('click', expect.any(Function));
    });

    it('should attach click event to notification button', () => {
      const notificationBtn = document.getElementById('audio-output-try-notification');
      const addEventListenerSpy = vi.spyOn(notificationBtn, 'addEventListener');

      Alert.attachEvents();

      expect(addEventListenerSpy).toHaveBeenCalledWith('click', expect.any(Function));
    });

    it('should play voiceChat sound when voicechat button is clicked', () => {
      const AudioSpy = vi.spyOn(globalThis, 'Audio');
      Alert.attachEvents();
      const voiceChatBtn = document.getElementById('audio-output-try-voicechat');

      voiceChatBtn.click();

      expect(AudioSpy).toHaveBeenCalledWith('src/audio/tryVoiceChatMale.mp3');
    });

    it('should play notification sound when notification button is clicked', () => {
      const AudioSpy = vi.spyOn(globalThis, 'Audio');
      Alert.attachEvents();
      const notificationBtn = document.getElementById('audio-output-try-notification');

      notificationBtn.click();

      expect(AudioSpy).toHaveBeenCalledWith('src/audio/tryNotificationMale.mp3');
    });
  });

  describe('play', () => {
    it('should create Audio instance with correct sound file', () => {
      const AudioSpy = vi.spyOn(globalThis, 'Audio');
      
      Alert.play('messageNew');

      expect(AudioSpy).toHaveBeenCalledWith('src/audio/messageNew.ogg');
    });

    it('should set volume from notification settings', () => {
      let capturedVolume;
      globalThis.Audio = class {
        _volume = 1;
        play = vi.fn();
        constructor(src) {
          this.src = src;
        }
        get volume() { return this._volume; }
        set volume(val) { 
          this._volume = val;
          capturedVolume = val;
        }
      };

      Alert.play('voiceUserJoin');

      expect(capturedVolume).toBe(0.5);
    });

    it('should call play on audio instance', () => {
      const mockPlay = vi.fn().mockResolvedValue(undefined);
      globalThis.Audio = class {
        volume = 1;
        play = mockPlay;

        constructor(src) {
          this.src = src;
        }
      };

      Alert.play('voiceUserLeft');

      expect(mockPlay).toHaveBeenCalled();
    });

    it('should log error for invalid notification type', () => {
      const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

      Alert.play('invalidType');

      expect(consoleErrorSpy).toHaveBeenCalledWith('Notification type is null or undefined');
    });

    it.each([
      'messageNew',
      'voiceUserJoin',
      'voiceUserLeft',
      'voiceConnected',
      'voiceDisconnected',
      'microphoneMuted',
      'microphoneActivated',
      'soundMuted',
      'soundActivated',
    ])('should handle %s sound type', (soundType) => {
      const mockPlay = vi.fn().mockResolvedValue(undefined);
      globalThis.Audio = class {
        volume = 1;
        play = mockPlay;

        constructor(src) {
          this.src = src;
        }
      };

      Alert.play(soundType);

      expect(mockPlay).toHaveBeenCalled();
    });
  });

  describe('private playTest method', () => {
    it('should set notification volume for notification type', () => {
      Alert.attachEvents();
      let capturedVolume;
      globalThis.Audio = class {
        _volume = 1;
        play = vi.fn();
        constructor(src) {
          this.src = src;
        }
        get volume() { return this._volume; }
        set volume(val) { 
          this._volume = val;
          capturedVolume = val;
        }
      };

      const notificationBtn = document.getElementById('audio-output-try-notification');
      notificationBtn.click();

      expect(capturedVolume).toBe(0.5);
    });

    it('should set voice volume for voiceChat type', () => {
      Alert.attachEvents();
      let capturedVolume;
      globalThis.Audio = class {
        _volume = 1;
        play = vi.fn();
        constructor(src) {
          this.src = src;
        }
        get volume() { return this._volume; }
        set volume(val) { 
          this._volume = val;
          capturedVolume = val;
        }
      };

      const voiceChatBtn = document.getElementById('audio-output-try-voicechat');
      voiceChatBtn.click();

      expect(capturedVolume).toBe(0.7);
    });

    it('should call play on audio instance for test sounds', () => {
      Alert.attachEvents();
      const mockPlay = vi.fn().mockResolvedValue(undefined);
      globalThis.Audio = class {
        volume = 1;
        play = mockPlay;

        constructor(src) {
          this.src = src;
        }
      };

      const notificationBtn = document.getElementById('audio-output-try-notification');
      notificationBtn.click();

      expect(mockPlay).toHaveBeenCalled();
    });
  });

  describe('volume settings integration', () => {
    it('should retrieve notification volume from RVC.userSettings()', () => {
      const mockUserSettings = {
        getNotificationVolume: function() { return 0.3; },
      };
      const getNotificationVolumeSpy = vi.spyOn(mockUserSettings, 'getNotificationVolume');
      const originalUserSettings = globalThis.RVC.userSettings;
      globalThis.RVC.userSettings = function() {
        return mockUserSettings;
      };

      let capturedVolume;
      globalThis.Audio = class {
        _volume = 1;
        play = vi.fn();
        constructor(src) {
          this.src = src;
        }
        get volume() { return this._volume; }
        set volume(val) { 
          this._volume = val;
          capturedVolume = val;
        }
      };

      Alert.play('messageNew');

      expect(getNotificationVolumeSpy).toHaveBeenCalled();
      expect(capturedVolume).toBe(0.3);
      
      globalThis.RVC.userSettings = originalUserSettings;
    });

    it('should retrieve voice volume from RVC.userSettings() for test sounds', () => {
      Alert.attachEvents();
      const mockUserSettings = {
        getVoiceVolume: function() { return 0.8; },
      };
      const getVoiceVolumeSpy = vi.spyOn(mockUserSettings, 'getVoiceVolume');
      const originalUserSettings = globalThis.RVC.userSettings;
      globalThis.RVC.userSettings = function() {
        return mockUserSettings;
      };

      let capturedVolume;
      globalThis.Audio = class {
        constructor(src) {
          this.src = src;
          this._volume = 1;
          this.play = vi.fn();
        }
        get volume() { return this._volume; }
        set volume(val) { 
          this._volume = val;
          capturedVolume = val;
        }
      };

      const voiceChatBtn = document.getElementById('audio-output-try-voicechat');
      voiceChatBtn.click();

      expect(getVoiceVolumeSpy).toHaveBeenCalled();
      expect(capturedVolume).toBe(0.8);
      
      globalThis.RVC.userSettings = originalUserSettings;
    });
  });
});