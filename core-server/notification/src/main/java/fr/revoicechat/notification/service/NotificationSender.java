package fr.revoicechat.notification.service;

import java.util.stream.Stream;

import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.notification.model.NotificationData;
import fr.revoicechat.notification.model.NotificationRegistrable;

public interface NotificationSender {
  /** Broadcast a message to all targeted users. */
  void send(Stream<? extends NotificationRegistrable> targetedUsers, NotificationData data);

  /** Ping a single user. */
  ActiveStatus ping(NotificationRegistrable registrable);
}
