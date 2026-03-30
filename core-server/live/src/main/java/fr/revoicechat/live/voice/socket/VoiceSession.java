package fr.revoicechat.live.voice.socket;

import java.util.UUID;
import jakarta.websocket.Session;

import fr.revoicechat.live.risk.LiveDiscussionRisks;
import fr.revoicechat.live.common.socket.SessionHolder;

public record VoiceSession(UUID user,
                           UUID room,
                           LiveDiscussionRisks risks,
                           Session session) implements SessionHolder {
  @Override
  public String sessionId() {
    return session.getId();
  }

  @Override
  public String toString() {
    return sessionId();
  }
}
