package fr.revoicechat.core.notification.service.user;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.notification.model.NotificationRegistrable;

public interface RoomUserFinder {

  /**
   * @param room currently, the user has no server, so no rooms.
   *             so we cannot know the user by room.
   */
  Stream<NotificationRegistrable> find(UUID room);
}
