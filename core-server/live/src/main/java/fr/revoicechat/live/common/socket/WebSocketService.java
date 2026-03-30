package fr.revoicechat.live.common.socket;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.Session;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.revoicechat.security.UserHolder;

/**
 * Core WebSocket service providing common functionality for WebSocket connection management.
 * This service handles authentication, message routing, session lifecycle, and asynchronous
 * task execution for WebSocket endpoints.
 *
 * <p>The service uses a {@link CompletionStageService} to queue and execute tasks asynchronously,
 * ensuring proper ordering and thread safety for WebSocket operations.</p>
 *
 * @see CompletionStageService
 * @see UserHolder
 */
@ApplicationScoped
public class WebSocketService {
  private static final Logger LOG = LoggerFactory.getLogger(WebSocketService.class);

  private final CompletionStageService completionStageService;
  private final ManagedExecutor executor;
  private final UserHolder userHolder;

  public WebSocketService(final CompletionStageService completionStageService,
                       final ManagedExecutor executor,
                       final UserHolder userHolder) {
    this.completionStageService = completionStageService;
    this.executor = executor;
    this.userHolder = userHolder;
  }

  public void onOpen(Session session, Consumer<String> handleOpen) {
    String token = token(session);
    if (token == null) {
      closeSession(session, CloseCodes.VIOLATED_POLICY, "Missing token");
      return;
    }
    UUID userId;
    try {
      userId = userHolder.peekId(token); // lightweight parse, no DB hit
    } catch (Exception e) {
      closeSession(session, CloseCodes.VIOLATED_POLICY, "Invalid token: " + e.getMessage());
      return;
    }
    // enqueue the actual heavy work
    completionStageService.enqueue(userId, session, () -> CompletableFuture.runAsync(() -> handleOpen.accept(token), executor));
  }

  public void onError(Session session, Throwable throwable) {
    LOG.error("Error on session {}: {}", session.getId(), throwable.getMessage());
  }

  public void onMessage(Object message, Session sender, Stream<Session> receivers) {
    LOG.trace("Client {} send : {}", sender.getId(), message);
    switch (message) {
      case String text   -> onMessage(receivers, text);
      case byte[] binary -> onMessage(receivers, binary);
      case null, default -> LOG.error("Invalid message from {}: {}", sender.getId(), message);
    }
  }

  private static void onMessage(final Stream<Session> receivers, final byte[] binary) {
    receivers.filter(Session::isOpen).forEach(session -> session.getAsyncRemote().sendBinary(ByteBuffer.wrap(binary)));
  }

  private static void onMessage(final Stream<Session> receivers, final String text) {
    receivers.filter(Session::isOpen).forEach(session -> session.getAsyncRemote().sendText(text));
  }

  public void closeSession(UUID userId, Session session, Runnable handler) {
    completionStageService.enqueue(userId, session, () -> CompletableFuture.runAsync(handler, executor));
  }

  public void closeSession(Session session, CloseCodes code, String reason) {
    IgnoreExceptions.run(() -> session.close(new CloseReason(code, reason)));
  }

  private String token(Session session) {
    return Optional.ofNullable(session.getUserProperties())
                   .map(props -> props.get("auth-token"))
                   .map(String.class::cast)
                   .orElseGet(() -> tokenFromQueryParam(session));
  }

  private static String tokenFromQueryParam(final Session session) {
    Map<String, List<String>> params = session.getRequestParameterMap();
    List<String> tokens = params.getOrDefault("token", List.of());
    if (tokens.isEmpty()) {
      return null;
    }
    return tokens.getFirst();
  }
}
