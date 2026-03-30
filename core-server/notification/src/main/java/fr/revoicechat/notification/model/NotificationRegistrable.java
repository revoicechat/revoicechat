package fr.revoicechat.notification.model;

import java.util.UUID;

public interface NotificationRegistrable {
  UUID getId();

  ActiveStatus getStatus();

  static NotificationRegistrable forId(UUID id) {
    return new OnlineNotificationRegistrable(id);
  }

  record OnlineNotificationRegistrable(UUID getId) implements NotificationRegistrable {
    @Override
    public ActiveStatus getStatus() {
      return ActiveStatus.ONLINE;
    }
  }
}
