package fr.revoicechat.notification;

import java.util.stream.Stream;

import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.notification.model.NotificationData;
import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.notification.service.NotificationSender;
import jakarta.enterprise.inject.spi.CDI;

public class Notification {
  private static final ThreadLocal<NotificationSender> holder = new ThreadLocal<>();

  private NotificationData data;

  private Notification() {}

  public static Notification of(NotificationPayload payload) {
    var notification = new Notification();
    notification.data = new NotificationData(payload);
    return notification;
  }

  public static ActiveStatus ping(NotificationRegistrable registrable) {
    return getNotificationSender().ping(registrable);
  }

  public void sendTo(NotificationRegistrable targetedUser) {
    sendTo(Stream.of(targetedUser));
  }

  public void sendTo(Stream<? extends NotificationRegistrable> targetedUsers) {
    getNotificationSender().send(targetedUsers, data);
  }

  private static NotificationSender getNotificationSender() {
    NotificationSender sender = holder.get();
    if (sender == null) {
      sender = CDI.current().select(NotificationSender.class).get();
      holder.set(sender);
    }
    return sender;
  }

  static void setNotificationSender(NotificationSender sender) {
    holder.set(sender);
  }

  static void cleanNotificationSender() {
    holder.remove();
  }
}
