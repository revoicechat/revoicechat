package fr.revoicechat.live.stream.socket;

import java.util.UUID;
import java.util.stream.Stream;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.revoicechat.live.common.service.DiscussionRiskService;
import fr.revoicechat.live.common.socket.WebSocketAuthConfigurator;
import fr.revoicechat.live.common.socket.WebSocketService;
import fr.revoicechat.live.stream.notification.StreamJoin;
import fr.revoicechat.live.stream.notification.StreamLeave;
import fr.revoicechat.live.stream.notification.StreamStart;
import fr.revoicechat.live.stream.notification.StreamStop;
import fr.revoicechat.live.voice.service.ConnectedUserRetriever;
import fr.revoicechat.live.voice.service.VoiceRoomUserFinder;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.security.UserHolder;

/**
 * WebSocket endpoint for managing video/audio streaming sessions.
 *
 * <p>This endpoint enables users to broadcast streams and view streams from others
 * within voice rooms. It distinguishes between streamers (broadcasters) and viewers,
 * managing their connections, permissions, and message routing.</p>
 *
 * <p>The endpoint is available at {@code /stream/{userId}/{name}} where:</p>
 * <ul>
 *   <li>{@code userId} - the ID of the user who is streaming</li>
 *   <li>{@code name} - the name/identifier of the stream</li>
 * </ul>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>One-to-many streaming (one streamer, multiple viewers)</li>
 *   <li>Permission-based access control for streaming and viewing</li>
 *   <li>Automatic stream cleanup when streamer disconnects</li>
 *   <li>Real-time notifications for stream lifecycle events</li>
 *   <li>Room-based stream isolation</li>
 * </ul>
 *
 * @see StreamSession
 * @see Streamer
 * @see Viewer
 * @see WebSocketService
 */
@ServerEndpoint(value = "/stream/{userId}/{name}", configurator = WebSocketAuthConfigurator.class)
@ApplicationScoped
public class StreamWebSocket {
  private static final Logger LOG = LoggerFactory.getLogger(StreamWebSocket.class);

  private final WebSocketService webSocketService;
  private final UserHolder userHolder;
  private final ConnectedUserRetriever connectedUserRetriever;
  private final DiscussionRiskService discussionRiskService;
  private final VoiceRoomUserFinder roomUserFinder;
  private final StreamSessions streamSessions;

  public StreamWebSocket(final WebSocketService webSocketService,
                         final UserHolder userHolder,
                         final ConnectedUserRetriever connectedUserRetriever,
                         final DiscussionRiskService discussionRiskService,
                         final VoiceRoomUserFinder roomUserFinder,
                         final StreamSessions streamSessions) {
    this.webSocketService = webSocketService;
    this.userHolder = userHolder;
    this.connectedUserRetriever = connectedUserRetriever;
    this.discussionRiskService = discussionRiskService;
    this.roomUserFinder = roomUserFinder;
    this.streamSessions = streamSessions;
  }

  /**
   * Called when a client opens a WebSocket connection to a stream.
   *
   * <p>Delegates to {@link WebSocketService#onOpen} for authentication,
   * then calls {@link #handleOpen} for stream-specific logic.</p>
   *
   * @param session the WebSocket session being opened
   * @param userId the user ID from the URL path (the streamer's ID)
   * @param name the stream name from the URL path
   */
  @OnOpen
  @SuppressWarnings("unused") // call by websocket listener
  public void onOpen(Session session, @PathParam("userId") String userId, @PathParam("name") String name) {
    webSocketService.onOpen(session, token -> handleOpen(session, token, userId, name));
  }

  /**
   * Handles the actual connection logic, determining if the user is starting a stream or joining one.
   *
   * <p>This method runs in a transactional context and performs the following:</p>
   * <ol>
   *   <li>Validates the user token</li>
   *   <li>Verifies the user is connected to a voice room</li>
   *   <li>Determines if the user is the streamer or a viewer</li>
   *   <li>Calls {@link #startStream} if the user is starting their own stream</li>
   *   <li>Calls {@link #joinStream} if the user is joining another user's stream</li>
   * </ol>
   *
   * @param session the WebSocket session
   * @param token the authentication token
   * @param userIdAsString the streamer's user ID as a string
   * @param streamName the name of the stream
   */
  @Transactional
  void handleOpen(Session session, String token, String userIdAsString, final String streamName) {
    UUID streamedUserId = UUID.fromString(userIdAsString);
    var user = userHolder.get(token);
    if (user == null) {
      webSocketService.closeSession(session, CloseCodes.VIOLATED_POLICY, "Invalid token");
      return;
    }
    var room = connectedUserRetriever.getRoomForUser(user.getId());
    if (room == null) {
      webSocketService.closeSession(session, CloseCodes.CANNOT_ACCEPT, "User is not connected to a room");
      return;
    }
    var risks = discussionRiskService.getStreamRisks(room, user.getId());
    if (streamedUserId.equals(user.getId())) {
      startStream(new Streamer(streamedUserId, streamName, risks, session), room);
    } else {
      joinStream(new Viewer(user.getId(), risks, session), streamedUserId, streamName, room);
    }
  }

  /**
   * Called when a text message is received from a client.
   *
   * <p>Routes the message from streamer to all authorized viewers.</p>
   *
   * @param message the text message content
   * @param sender the session that sent the message
   */
  @OnMessage
  @SuppressWarnings("unused") // call by websocket listener
  public void onMessage(String message, Session sender) {
    webSocketService.onMessage(message, sender, getReceiver(sender));
  }

  /**
   * Called when a binary message (stream data) is received from a client.
   *
   * <p>Routes the stream data from streamer to all authorized viewers.</p>
   *
   * @param message the binary message content (typically video/audio data)
   * @param sender the session that sent the message
   */
  @OnMessage
  @SuppressWarnings("unused") // call by websocket listener
  public void onMessage(byte[] message, Session sender) {
    webSocketService.onMessage(message, sender, getReceiver(sender));
  }

  /**
   * Determines which sessions should receive stream data from the sender.
   *
   * <p>Only the streamer can send data, and only viewers with receive permissions
   * will get the data. The sender is excluded from receivers.</p>
   *
   * @param sender the session sending the stream data (must be the streamer)
   * @return stream of viewer sessions that should receive the data
   */
  private Stream<Session> getReceiver(Session sender) {
    var current = streamSessions.get(sender);
    if (current == null || !current.streamer().risks().send()) {
      return Stream.empty();
    }
    return current.viewers()
                  .stream()
                  .filter(viewer -> viewer.risks().receive())
                  .map(Viewer::session)
                  .filter(session -> !session.getId().equals(sender.getId()));
  }

  /**
   * Called when a WebSocket connection is closed.
   *
   * <p>Handles two scenarios:</p>
   * <ul>
   *   <li>If the streamer disconnects: stops the entire stream, disconnecting all viewers</li>
   *   <li>If a viewer disconnects: removes only that viewer from the stream</li>
   * </ul>
   *
   * @param session the session being closed
   */
  @OnClose
  @SuppressWarnings("unused") // call by websocket listener
  public void onClose(Session session, CloseReason closeReason) {
    var stream = streamSessions.get(session);
    if (stream != null) {
      var user = stream.streamer().user();
      var room = connectedUserRetriever.getRoomForUser(user);
      webSocketService.closeSession(user, session, () -> stopStream(stream, room));
      LOG.debug("WebSocket streamer {} closed - Code: {}, Reason: {}", user, closeReason.getCloseCode(), closeReason.getReasonPhrase());
    } else {
      var closingViewer = getClosingViewer(session);
      if (closingViewer != null) {
        var user = closingViewer.viewer().user();
        webSocketService.closeSession(closingViewer.viewer().user(), session, () -> leaveStream(closingViewer));
        LOG.debug("WebSocket viewer {} closed - Code: {}, Reason: {}", user, closeReason.getCloseCode(), closeReason.getReasonPhrase());
      }
    }
  }

  /**
   * Starts a new stream for the given streamer.
   *
   * <p>If a stream with the same user and name already exists, it is stopped first.
   * Notifies all room participants that a new stream has started.</p>
   *
   * @param streamer the streamer starting the broadcast
   * @param roomId the room where the stream is happening
   */
  private void startStream(Streamer streamer, UUID roomId) {
    if (!streamer.risks().send()) {
      webSocketService.closeSession(streamer.session(), CloseCodes.CANNOT_ACCEPT, "User in not allowed to stream in this room");
      return;
    }
    var user = streamer.user();
    var stream = streamSessions.get(user, streamer.streamName());
    stopStream(stream, roomId);
    streamSessions.addSession(new StreamSession(streamer));
    LOG.info("Streamer connected: {}", user);
    Notification.of(new StreamStart(streamer.user(), streamer.streamName())).sendTo(roomUserFinder.find(roomId));
  }

  /**
   * Adds a viewer to an existing stream.
   *
   * <p>Validates that the viewer has permission to receive stream data and that
   * the requested stream exists. Notifies room participants of the new viewer.</p>
   *
   * @param viewer the viewer joining the stream
   * @param streamedUserId the ID of the user who is streaming
   * @param streamName the name of the stream
   * @param roomId the room where the stream is happening
   */
  private void joinStream(Viewer viewer, UUID streamedUserId, String streamName, UUID roomId) {
    if (!viewer.risks().receive()) {
      webSocketService.closeSession(viewer.session(), CloseCodes.CANNOT_ACCEPT, "User in not allowed to watch a stream in this room");
      return;
    }
    var user = viewer.user();
    var stream = streamSessions.get(streamedUserId, streamName);
    if (stream == null) {
      webSocketService.closeSession(viewer.session(), CloseCodes.CANNOT_ACCEPT, "No stream found");
      return;
    }
    stream.viewers().add(viewer);
    LOG.info("Viewer connected: {}", user);
    Notification.of(new StreamJoin(streamedUserId, streamName, user)).sendTo(roomUserFinder.find(roomId));
  }

  /**
   * Removes a viewer from a stream when they disconnect.
   *
   * <p>Locates the viewer's session, removes them from the stream's viewer list,
   * and notifies room participants that the viewer has left.</p>
   */
  @Transactional
  void leaveStream(ClosingViewer closingViewer) {
    handleCloseSession(closingViewer.viewer());
    closingViewer.stream().remove(closingViewer.viewer());
    var streamer = closingViewer.stream().streamer();
    var room = connectedUserRetriever.getRoomForUser(streamer.user());
    Notification.of(new StreamLeave(streamer.user(), streamer.streamName(), closingViewer.viewer().user())).sendTo(roomUserFinder.find(room));
  }

  /**
   * Stops a stream completely, disconnecting all viewers and the streamer.
   *
   * <p>Closes all viewer sessions, closes the streamer session, removes the stream
   * from active streams, and notifies room participants that the stream has ended.</p>
   *
   * @param stream the stream session to stop
   * @param roomId the room where the stream was happening
   */
  @Transactional
  void stopStream(StreamSession stream, UUID roomId) {
    if (stream != null) {
      LOG.info("Streamer stopping: {}", stream);
      streamSessions.removeSession(stream);
      stream.viewers().forEach(this::handleCloseSession);
      handleCloseSession(stream.streamer());
      Notification.of(new StreamStop(stream.streamer().user(), stream.streamer().streamName())).sendTo(roomUserFinder.find(roomId));
      LOG.info("Streamer stop: {}", stream);
    }
  }

  /**
   * Finds a viewer session that is being closed.
   *
   * <p>Searches through all active streams to find which stream contains
   * the given viewer session.</p>
   *
   * @param session the viewer session being closed
   * @return a record containing the stream and viewer, or null if not found
   */
  private ClosingViewer getClosingViewer(final Session session) {
    for (final StreamSession streamSession : streamSessions.getAll()) {
      for (final Viewer viewer : streamSession.viewers()) {
        if (viewer.is(session)) {
          return new ClosingViewer(streamSession, viewer);
        }
      }
    }
    return null;
  }

  /**
   * Helper record for tracking a viewer that is leaving a stream.
   *
   * @param stream the stream session
   * @param viewer the viewer leaving
   */
  private record ClosingViewer(StreamSession stream, Viewer viewer) {}

  /**
   * Handles the actual session close logic for a stream participant.
   *
   * <p>Logs the disconnection and closes the WebSocket session cleanly.</p>
   *
   * @param user the stream agent (streamer or viewer) being disconnected
   */
  @Transactional
  void handleCloseSession(final StreamAgent user) {
    LOG.info("Client disconnected: {}", user);
    webSocketService.closeSession(user.session(), CloseCodes.NORMAL_CLOSURE, "Client disconnected");
  }

  /**
   * Called when an error occurs on a WebSocket connection.
   *
   * <p>Delegates error handling to {@link WebSocketService}.</p>
   *
   * @param session the session where the error occurred
   * @param throwable the error
   */
  @OnError
  @SuppressWarnings("unused") // call by websocket listener
  public void onError(Session session, Throwable throwable) {
    webSocketService.onError(session, throwable);
  }
}
