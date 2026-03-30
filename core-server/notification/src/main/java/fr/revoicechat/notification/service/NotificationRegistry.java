package fr.revoicechat.notification.service;

import jakarta.ws.rs.sse.SseEventSink;

import fr.revoicechat.notification.model.NotificationRegistrable;

public interface NotificationRegistry {
  /** register a user for SSE streaming. */
  void register(NotificationRegistrable registrable, SseEventSink sink);
}
