package fr.revoicechat.live.common.socket;

import jakarta.websocket.Session;

public interface SessionHolder {
  Session session();
  String sessionId();

  default boolean is(Session session) {
    return sessionId().equals(session.getId());
  }
}
