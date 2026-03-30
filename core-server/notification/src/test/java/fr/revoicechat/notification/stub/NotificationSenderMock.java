package fr.revoicechat.notification.stub;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.notification.model.NotificationData;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.notification.service.NotificationSender;

public class NotificationSenderMock implements NotificationSender {
  private final List<NotificationSent> notifications = new ArrayList<>();
  private final List<NotificationRegistrable> ping = new ArrayList<>();

  @Override
  public void send(Stream<? extends NotificationRegistrable> targetedUsers, NotificationData data) {
    targetedUsers.forEach(user -> notifications.add(new NotificationSent(user, data)));
  }

  @Override
  public ActiveStatus ping(final NotificationRegistrable registrable) {
    ping.add(registrable);
    return ActiveStatus.ONLINE;
  }

  public List<NotificationSent> getNotifications() {
    return notifications;
  }

  public List<NotificationRegistrable> getPing() {
    return ping;
  }

  public record NotificationSent(NotificationRegistrable registrable, NotificationData data) {}
}