package fr.revoicechat.notification.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.revoicechat.notification.Notification;
import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.notification.model.NotificationRegistrableUser;
import fr.revoicechat.notification.service.NotificationService;
import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.model.DefaultAuthenticatedUser;
import fr.revoicechat.security.service.SecurityTokenService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.sse.InboundSseEvent;
import jakarta.ws.rs.sse.SseEventSource;

@QuarkusTest
class TestNotificationController {
  private static final String ID_USER = "35117c82-3b6f-403f-be5a-f3ee842d97d6";
  private static final Logger LOG = LoggerFactory.getLogger(TestNotificationController.class);

  @Inject NotificationService service;
  @Inject SecurityTokenService securityTokenService;
  @Inject MockUserCreator mockUserCreator;

  @Test
  void test() throws Exception {
    mockUserCreator.createUser();
    var user = new DefaultAuthenticatedUser();
    user.setId(UUID.fromString(ID_USER));
    var token = securityTokenService.generate(user);
    List<String> events = new ArrayList<>();
    try (Client client = ClientBuilder.newBuilder()
                                      .register((ClientRequestFilter) requestContext -> requestContext.getHeaders().add("Authorization", "Bearer " + token))
                                      .build();
         var _ = source(client, events)) {
      assertThat(events).isEmpty();
      assertThat(service.getProcessor(UUID.fromString(ID_USER))).hasSize(1);
      Notification.ping(NotificationRegistrable.forId(UUID.fromString(ID_USER)));
      await().during(1, SECONDS);
      assertThat(events).containsExactly("{\"type\":\"PING\",\"data\":{}}");
    }
  }

  private SseEventSource source(final Client client, List<String> events) throws Exception {
    URI uri = new URI("http://localhost:8081/api/sse");
    CountDownLatch latch = new CountDownLatch(1);
    var target = client.target(uri);
    SseEventSource source = SseEventSource.target(target).build();
    source.register(
        (InboundSseEvent event) -> {
          events.add(event.readData());
          latch.countDown();
        },
        ex -> LOG.error("error", ex),
        () -> LOG.info("Stream closed")
    );
    source.open();
    // wait for the first event
    if (latch.await(2, SECONDS)) {
      return source;
    }
    return null;
  }

  @ApplicationScoped
  static class MockUserCreator {
    @Inject EntityManager entityManager;

    @Transactional
    public void createUser() {
      NotificationRegistrableUser user = new NotificationRegistrableUser();
      user.setId(UUID.fromString(ID_USER));
      user.setStatus(ActiveStatus.ONLINE);
      entityManager.persist(user);
    }
  }
}