package fr.revoicechat.notification.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.jboss.resteasy.plugins.providers.sse.SseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.revoicechat.notification.Notification;
import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.notification.model.NotificationData;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.notification.data.UserStatusUpdate;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import jakarta.ws.rs.sse.SseEventSink;

/**
 * Service that manages user notification via Server-Sent Events (SSE).
 * <p>
 * This service allows clients to:
 * <ul>
 *     <li>Retrieve all messages for a given chat room</li>
 *     <li>Register for real-time message updates using SSE</li>
 *     <li>Send messages to a room and broadcast them to connected clients</li>
 * </ul>
 * <p>
 * SSE emitters are stored in-memory per room and removed automatically when a connection
 * completes, times out, or encounters an error.
 */
@Singleton
public class NotificationService implements NotificationRegistry, NotificationSender {
  private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

  private final Map<UUID, Collection<SseHolder>> processors = new ConcurrentHashMap<>();

  @Override
  public void register(NotificationRegistrable registrable, SseEventSink sink) {
    var id = registrable.getId();
    getProcessor(id).add(new SseHolder(sink));
    notifyConnection(registrable);
  }

  private void notifyConnection(NotificationRegistrable registrable) {
    if (getProcessor(registrable.getId()).size() == 1) {
      LOG.debug("user {} connected", registrable.getId());
      Notification.of(new UserStatusUpdate(registrable.getId(), registrable.getStatus())).sendTo(getAllUsersExcept(registrable.getId()));
    }
  }

  @Override
  public void send(Stream<? extends NotificationRegistrable> targetedUsers, NotificationData data) {
    targetedUsers.filter(this::hasSseHolders).forEach(user -> sendAndCloseIfNecessary(data, user));
  }

  /**
   * the SSE holder in wrap in another concurrent hashmap
   */
  @Override
  public ActiveStatus ping(NotificationRegistrable registrable) {
    var id = registrable.getId();
    LOG.debug("ping user {}", id);
    var holders = getProcessor(registrable.getId());
    for (SseHolder holder : new HashSet<>(holders)) {
      if (holder.send(NotificationData.ping())) {
        return registrable.getStatus();
      } else {
        holders.remove(holder);
      }
    }
    return ActiveStatus.OFFLINE;
  }

  private boolean hasSseHolders(NotificationRegistrable registrable) {
    return !getProcessor(registrable.getId()).isEmpty();
  }

  private void sendAndCloseIfNecessary(NotificationData notificationData, NotificationRegistrable registrable) {
    var holders = getProcessor(registrable.getId());
    for (SseHolder holder : new HashSet<>(holders)) {
      LOG.debug("send message to user {}", registrable.getId());
      if (!holder.send(notificationData)) {
        LOG.debug("sse closed for user {}", registrable.getId());
        holders.remove(holder);
      }
    }
    notifyDisconnection(registrable);
  }

  private void notifyDisconnection(NotificationRegistrable registrable) {
    if (getProcessor(registrable.getId()).isEmpty()) {
      LOG.debug("user {} disconnected", registrable.getId());
      Notification.of(new UserStatusUpdate(registrable.getId(), ActiveStatus.OFFLINE)).sendTo(getAllUsersExcept(registrable.getId()));
    }
  }

  private Stream<NotificationRegistrable> getAllUsersExcept(final UUID id) {
    return processors.keySet().stream().filter(uuid -> !uuid.equals(id)).map(NotificationRegistrable::forId);
  }

  public Collection<SseHolder> getProcessor(UUID userId) {
    return processors.computeIfAbsent(userId, _ -> Collections.synchronizedSet(new HashSet<>()));
  }

  @PreDestroy
  public void shutdownSseEmitters() {
    LOG.info("Closing all SSE connections..");
    processors.values().stream().flatMap(Collection::stream).forEach(SseHolder::close);
    processors.clear();
  }

  public record SseHolder(SseEventSink sink) {
    boolean send(NotificationData data) {
      try {
        sink.send(new SseImpl().newEventBuilder().data(data).build());
        return true;
      } catch (Exception _) {
        sink.close();
        return false;
      }
    }
    void close() {sink.close();}
  }
}
