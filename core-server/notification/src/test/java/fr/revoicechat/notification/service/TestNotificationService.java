package fr.revoicechat.notification.service;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.notification.model.NotificationData;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.notification.data.UserStatusUpdate;
import fr.revoicechat.notification.service.NotificationService.SseHolder;
import fr.revoicechat.notification.stub.NotificationPayloadMock;
import fr.revoicechat.notification.stub.SseEventSinkMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class TestNotificationService {

  @Inject NotificationService service;

  @AfterEach
  void tearDown() {
    service.shutdownSseEmitters();
  }

  @Test
  void testRegister() {
    // Given
    var registrable1 = NotificationRegistrable.forId(UUID.randomUUID());
    var registrable2 = NotificationRegistrable.forId(UUID.randomUUID());
    var registrable3 = NotificationRegistrable.forId(UUID.randomUUID());
    var sink1 = new SseEventSinkMock();
    var sink2 = new SseEventSinkMock();
    var sink3 = new SseEventSinkMock();
    // When
    service.register(registrable1, sink1);
    service.register(registrable1, sink2);
    service.register(registrable2, sink3);
    // Then
    assertThat(service.getProcessor(registrable1.getId())).hasSize(2).map(SseHolder::sink).containsExactlyInAnyOrder(sink1, sink2);
    assertThat(service.getProcessor(registrable2.getId())).hasSize(1).map(SseHolder::sink).containsExactlyInAnyOrder(sink3);
    assertThat(service.getProcessor(registrable3.getId())).isEmpty();
    assertThat(sink1.getEvents()).hasSize(1);
    assertThat(get(sink1, 0).data()).isInstanceOf(UserStatusUpdate.class);
    assertThat(sink2.getEvents()).hasSize(1);
    assertThat(get(sink2, 0).data()).isInstanceOf(UserStatusUpdate.class);
    assertThat(sink3.getEvents()).isEmpty();
  }

  @Test
  void testSend() {
    // Given
    var registrable1 = NotificationRegistrable.forId(UUID.randomUUID());
    var sink1 = new SseEventSinkMock();
    var sink2 = new SseEventSinkMock();
    sink2.close();
    // When
    service.register(registrable1, sink1);
    service.register(registrable1, sink2);
    service.send(Stream.of(registrable1), new NotificationData(new NotificationPayloadMock("test")));
    // Then
    assertThat(service.getProcessor(registrable1.getId())).hasSize(1).map(SseHolder::sink).containsExactlyInAnyOrder(sink1);
    assertThat(sink2.isClosed()).isTrue();
    assertThat(sink1.isClosed()).isFalse();
    assertThat(sink1.getEvents()).hasSize(1);
    assertThat(get(sink1, 0).data()).isInstanceOf(NotificationPayloadMock.class);
  }

  @Test
  void testSendOnNoSse() {
    // Given
    var registrable1 = NotificationRegistrable.forId(UUID.randomUUID());
    var targetedUsers = Stream.of(registrable1);
    var data = new NotificationData(new NotificationPayloadMock("test"));
    // When
    assertThatCode(() -> service.send(targetedUsers, data)).doesNotThrowAnyException();
    assertThat(service.getProcessor(registrable1.getId())).isEmpty();
  }

  @Test
  void testOffline() {
    // Given
    var registrable1 = NotificationRegistrable.forId(UUID.randomUUID());
    var registrable2 = NotificationRegistrable.forId(UUID.randomUUID());
    var sink1 = new SseEventSinkMock();
    var sink2 = new SseEventSinkMock();
    // When
    service.register(registrable1, sink1);
    service.register(registrable2, sink2);
    // Then
    assertThat(sink1.getEvents()).hasSize(1);
    assertThat(get(sink1, 0).data()).isInstanceOf(UserStatusUpdate.class);
    assertThat(sink2.getEvents()).isEmpty();
    // When
    sink2.close();
    service.send(Stream.of(registrable2), new NotificationData(new NotificationPayloadMock("test")));
    // Then
    assertThat(sink2.getEvents()).isEmpty();
    assertThat(sink1.getEvents()).hasSize(2);
    assertThat(get(sink1, 0).data()).isInstanceOf(UserStatusUpdate.class);
    assertThat(get(sink1, 1).data()).isInstanceOf(UserStatusUpdate.class);
  }

  @Test
  void testPingWithNoRegistry() {
    var registrable1 = NotificationRegistrable.forId(UUID.randomUUID());
    assertThat(new NotificationService().ping(registrable1)).isEqualTo(ActiveStatus.OFFLINE);
  }

  @Test
  void testPingWithOneRegistry() {
    var registrable1 = NotificationRegistrable.forId(UUID.randomUUID());
    var sink1 = new SseEventSinkMock();
    service.register(registrable1, sink1);
    assertThat(service.ping(registrable1)).isEqualTo(ActiveStatus.ONLINE);
  }

  @Test
  void testPingWithOneRegistryCloseAndOneOpen() {
    var registrable1 = NotificationRegistrable.forId(UUID.randomUUID());
    var sink1 = new SseEventSinkMock();
    var sink2 = new SseEventSinkMock();
    sink2.close();
    service.register(registrable1, sink1);
    service.register(registrable1, sink2);
    assertThat(service.ping(registrable1)).isEqualTo(ActiveStatus.ONLINE);
  }

  @Test
  void testPingWithOnlyClosedRegistry() {
    var registrable1 = NotificationRegistrable.forId(UUID.randomUUID());
    var sink1 = new SseEventSinkMock();
    sink1.close();
    var sink2 = new SseEventSinkMock();
    sink2.close();
    service.register(registrable1, sink1);
    service.register(registrable1, sink2);
    assertThat(service.ping(registrable1)).isEqualTo(ActiveStatus.OFFLINE);
  }

  @Test
  void testShutdownSseEmitters() {
    var registrable1 = NotificationRegistrable.forId(UUID.randomUUID());
    var sink1 = new SseEventSinkMock();
    var sink2 = new SseEventSinkMock();
    service.register(registrable1, sink1);
    service.register(registrable1, sink2);
    service.shutdownSseEmitters();
    assertThat(sink1.isClosed()).isTrue();
    assertThat(sink2.isClosed()).isTrue();
  }

  private NotificationData get(SseEventSinkMock sink, int index) {
    return (NotificationData) sink.getEvents().get(index).getData();
  }
}