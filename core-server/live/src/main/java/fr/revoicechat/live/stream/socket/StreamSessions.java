package fr.revoicechat.live.stream.socket;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.revoicechat.live.stream.representation.StreamRepresentation;
import fr.revoicechat.live.stream.service.StreamRetriever;
import jakarta.inject.Singleton;
import jakarta.websocket.Session;

@Singleton
public class StreamSessions implements StreamRetriever {
  private static final Logger LOG = LoggerFactory.getLogger(StreamSessions.class);
  // Thread-safe set of connected sessions
  private static final Set<StreamSession> streams = ConcurrentHashMap.newKeySet();

  Set<StreamSession> getAll() {
    return streams;
  }

  void addSession(StreamSession session) {
    streams.add(session);
  }

  void removeSession(StreamSession session) {
    LOG.info("Stream size before: {}", streams.size());
    boolean removed = streams.remove(session);
    LOG.info("Removed: {}, Stream size after: {}", removed, streams.size());
  }

  /**
   * Finds a stream session by the streamer's WebSocket session.
   *
   * @param session the streamer's WebSocket session
   * @return the stream session, or null if not found
   */
  StreamSession get(Session session) {
    return streams.stream()
                  .filter(e -> e.streamer().is(session))
                  .findFirst()
                  .orElse(null);
  }

  /**
   * Finds a stream session by streamer ID and stream name.
   *
   * @param streamedUserId the streamer's user ID
   * @param streamName the stream name
   * @return the stream session, or null if not found
   */
  StreamSession get(UUID streamedUserId, String streamName) {
    return streams.stream()
                  .filter(e -> e.streamer().is(streamedUserId, streamName))
                  .findFirst()
                  .orElse(null);
  }

  @Override
  public List<StreamRepresentation> fetch(final UUID userId) {
    return streams.stream()
                  .filter(streamSession -> streamSession.isOwnedBy(userId))
                  .map(StreamSession::toRepresentation)
                  .toList();
  }
}
