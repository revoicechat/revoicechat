package fr.revoicechat.live.stub;

import java.util.UUID;

import fr.revoicechat.live.voice.service.VoiceRoomPredicate;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VoiceRoomPredicateMock implements VoiceRoomPredicate {
  @Override
  public boolean isVoiceRoom(final UUID roomId) {
    return true;
  }
}
