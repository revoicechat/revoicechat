package fr.revoicechat.risk.service.user;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.notification.model.NotificationRegistrable;

public interface UserServerFinder {
  Stream<NotificationRegistrable> findUserForServer(UUID serverId);
}
