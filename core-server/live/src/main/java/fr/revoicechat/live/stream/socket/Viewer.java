package fr.revoicechat.live.stream.socket;

import java.util.UUID;
import jakarta.websocket.Session;

import fr.revoicechat.live.risk.LiveDiscussionRisks;

record Viewer(UUID user, LiveDiscussionRisks risks, Session session) implements StreamAgent {
  @Override
  public String sessionId() {
    return session.getId();
  }

  @Override
  public String toString() {
    return sessionId();
  }
}