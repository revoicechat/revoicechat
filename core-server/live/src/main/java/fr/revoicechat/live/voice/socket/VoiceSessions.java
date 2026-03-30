package fr.revoicechat.live.voice.socket;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import jakarta.inject.Singleton;
import jakarta.websocket.Session;

import fr.revoicechat.live.voice.service.ConnectedUserRetriever;

@Singleton
public class VoiceSessions implements ConnectedUserRetriever {
  // Thread-safe set of connected sessions
  private static final Set<VoiceSession> sessions = ConcurrentHashMap.newKeySet();

  Stream<VoiceSession> getAll() {
    return sessions.stream();
  }

  void addSession(VoiceSession session) {
    sessions.add(session);
  }

  void removeSession(VoiceSession session) {
    sessions.remove(session);
  }

  Stream<Session> getReceiver(Session sender) {
    var current = getExistingSession(sender);
    if (current == null || !current.risks().send()) {
      return Stream.empty();
    }
    return sessions.stream()
                   .filter(voiceSession -> voiceSession.room().equals(current.room()))
                   .filter(voiceSession -> voiceSession.risks().receive())
                   .map(VoiceSession::session)
                   .filter(session -> !session.getId().equals(sender.getId()));
  }

  /**
   * Finds an existing session for a given user ID.
   *
   * @param user the user ID to search for
   * @return the user's voice session, or null if not found
   */
  VoiceSession getExistingSession(UUID user) {
    return sessions.stream().filter(voiceSession -> voiceSession.user().equals(user))
                   .findFirst()
                   .orElse(null);
  }

  /**
   * Finds an existing session matching a given WebSocket session.
   *
   * @param session the WebSocket session to search for
   * @return the corresponding voice session, or null if not found
   */
  VoiceSession getExistingSession(Session session) {
    return sessions.stream().filter(voiceSession -> voiceSession.is(session))
                   .findFirst()
                   .orElse(null);
  }

  @Override
  public Stream<UUID> getConnectedUsers(UUID room) {
    return sessions.stream()
                   .filter(voiceSession -> voiceSession.room().equals(room))
                   .map(VoiceSession::user);
  }

  @Override
  public UUID getRoomForUser(final UUID user) {
    return sessions.stream()
                   .filter(voiceSession -> voiceSession.user().equals(user))
                   .findFirst()
                   .map(VoiceSession::room)
                   .orElse(null);
  }
}
