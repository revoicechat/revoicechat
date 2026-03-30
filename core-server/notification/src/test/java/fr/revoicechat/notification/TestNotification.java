package fr.revoicechat.notification;

import java.util.UUID;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.revoicechat.notification.model.NotificationData;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.notification.stub.NotificationPayloadMock;
import fr.revoicechat.notification.stub.NotificationSenderMock;
import fr.revoicechat.notification.stub.NotificationSenderMock.NotificationSent;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestNotification {

  @AfterEach
  void tearDown() {
    Notification.cleanNotificationSender();
  }

  @Test
  void testNotificationSend() {
    var sender = new NotificationSenderMock();
    Notification.setNotificationSender(sender);
    var registrable1 = NotificationRegistrable.forId(UUID.randomUUID());
    var registrable2 = NotificationRegistrable.forId(UUID.randomUUID());
    var registrable3 = NotificationRegistrable.forId(UUID.randomUUID());
    var payload = new NotificationPayloadMock("test");
    Notification.of(payload).sendTo(Stream.of(registrable1, registrable2));
    Assertions.assertThat(sender.getNotifications()).hasSize(2)
              .containsExactly(new NotificationSent(registrable1, new NotificationData(payload)),
                  new NotificationSent(registrable2, new NotificationData(payload)))
              .map(NotificationSent::registrable).doesNotContain(registrable3);
  }

  @Test
  void testPing() {
    var sender = new NotificationSenderMock();
    Notification.setNotificationSender(sender);
    var registrable1 = NotificationRegistrable.forId(UUID.randomUUID());
    var registrable2 = NotificationRegistrable.forId(UUID.randomUUID());
    Notification.ping(registrable1);
    Assertions.assertThat(sender.getPing()).hasSize(1)
              .containsExactly(registrable1)
              .doesNotContain(registrable2);
  }
}