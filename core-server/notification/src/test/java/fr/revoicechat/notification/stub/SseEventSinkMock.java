package fr.revoicechat.notification.stub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.SseEventSink;

public class SseEventSinkMock implements SseEventSink {
  private final List<OutboundSseEvent> events = new ArrayList<>();
  private boolean closed = false;

  @Override
  public boolean isClosed() {
    return closed;
  }

  @Override
  public CompletionStage<?> send(final OutboundSseEvent outboundSseEvent) {
    if (closed) {
      throw new IllegalStateException("Sse event sink is closed");
    }
    events.add(outboundSseEvent);
    return null;
  }

  @Override
  public void close() {
    closed = true;
  }

  public List<OutboundSseEvent> getEvents() {
    return events;
  }
}