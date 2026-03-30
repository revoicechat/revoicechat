package fr.revoicechat.core.notification.service;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.notification.service.user.RoomUserFinder;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.risk.service.user.UserServerFinder;
import jakarta.enterprise.inject.spi.CDI;

public final class NotificationUserRetriever {
  private static final ThreadLocal<UserServerFinder> userServerFinder = new ThreadLocal<>();
  private static final ThreadLocal<RoomUserFinder> roomUserFinder = new ThreadLocal<>();

  private NotificationUserRetriever() {/* not instantiable */}

  public static Stream<NotificationRegistrable> findUserForServer(UUID serverId) {
    return getUserServerFinder().findUserForServer(serverId);
  }

  public static Stream<NotificationRegistrable> findUserForRoom(UUID roomId) {
    return getRoomUserFinder().find(roomId);
  }

  private static UserServerFinder getUserServerFinder() {
    UserServerFinder sender = userServerFinder.get();
    if (sender == null) {
      sender = CDI.current().select(UserServerFinder.class).get();
      userServerFinder.set(sender);
    }
    return sender;
  }

  private static RoomUserFinder getRoomUserFinder() {
    RoomUserFinder sender = roomUserFinder.get();
    if (sender == null) {
      sender = CDI.current().select(RoomUserFinder.class).get();
      roomUserFinder.set(sender);
    }
    return sender;
  }

  static void cleanThreadLocals() {
    userServerFinder.remove();
    roomUserFinder.remove();
  }
}
