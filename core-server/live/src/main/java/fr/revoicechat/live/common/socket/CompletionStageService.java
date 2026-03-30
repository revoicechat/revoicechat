package fr.revoicechat.live.common.socket;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.Session;

@Singleton
public class CompletionStageService {
  private static final Logger LOG = LoggerFactory.getLogger(CompletionStageService.class);

  // One queue per user (serialized execution)
  private final ConcurrentHashMap<UUID, CompletableFuture<Void>> userQueues = new ConcurrentHashMap<>();

  private final ManagedExecutor executor;

  public CompletionStageService(final ManagedExecutor executor) {
    this.executor = executor;
  }

  /**
   * Utility: enqueue a task for a specific user, so tasks run sequentially.
   */
  public void enqueue(UUID userId, Session session, Supplier<CompletionStage<Void>> task) {
    userQueues.compute(userId, (id, prev) -> {
      CompletableFuture<Void> start = (prev == null ? CompletableFuture.completedFuture(null) : prev);
      return start
          .thenComposeAsync(v -> task.get(), executor)
          .exceptionally(t -> {
            LOG.error("Error handling user {}", userId, t);
            closeSession(session, CloseCodes.PROTOCOL_ERROR, "Error handling user " + userId);
            return null;
          });
    });
  }

  private void closeSession(Session session, CloseCodes code, String reason) {
    IgnoreExceptions.run(() -> session.close(new CloseReason(code, reason)));
  }
}
