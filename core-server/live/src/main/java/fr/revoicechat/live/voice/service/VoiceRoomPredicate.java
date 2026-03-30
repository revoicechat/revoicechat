package fr.revoicechat.live.voice.service;

import java.util.UUID;

public interface VoiceRoomPredicate {

  boolean isVoiceRoom(UUID roomId);
}
