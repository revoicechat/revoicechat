package fr.revoicechat.live.stream.socket;

import java.util.Objects;
import java.util.UUID;
import jakarta.websocket.Session;

import fr.revoicechat.live.risk.LiveDiscussionRisks;

record Streamer(UUID user, String streamName, LiveDiscussionRisks risks, Session session) implements StreamAgent {

  @Override
  public String sessionId() {
    return session.getId();
  }

  public boolean is(final UUID user, final String streamName) {
    return Objects.equals(this.user, user) && Objects.equals(this.streamName, streamName);
  }

  @Override
  public String toString() {
    return sessionId();
  }
}