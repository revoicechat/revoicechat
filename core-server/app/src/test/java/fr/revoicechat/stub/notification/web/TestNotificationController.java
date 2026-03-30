package fr.revoicechat.stub.notification.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import fr.revoicechat.notification.model.NotificationRegistrable;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.InboundSseEvent;
import jakarta.ws.rs.sse.SseEventSource;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.representation.UserRepresentation;
import fr.revoicechat.core.web.tests.RestTestUtils;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.notification.service.NotificationService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;

@QuarkusTest
@TestProfile(BasicIntegrationTestProfile.class)
@CleanDatabase
class TestNotificationController {
  private static final Logger LOG = LoggerFactory.getLogger(TestNotificationController.class);

  @Inject NotificationService service;

  @Test
  void test() throws Exception {
    var user = RestTestUtils.signup("user", "psw");
    String token = RestTestUtils.login("user", "psw");
    List<String> events = new ArrayList<>();
    try (Client client = ClientBuilder.newBuilder()
                                      .register((ClientRequestFilter) requestContext -> requestContext.getHeaders().add("Authorization", "Bearer " + token))
                                      .build();
         var _ = source(client, events)) {
      assertThat(events).isEmpty();
      assertThat(service.getProcessor(user.id())).hasSize(1);
      await().during(3, SECONDS);
      Notification.ping(NotificationRegistrable.forId(user.id()));
      await().atMost(10, SECONDS).until(() -> !events.isEmpty());
      assertThat(events).containsExactly("{\"type\":\"PING\",\"data\":{}}");
      var retrievedUser = RestAssured.given()
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .header("Authorization", "Bearer " + token)
                                     .when().get("/user/me")
                                     .then().statusCode(200)
                                     .extract().body().as(UserRepresentation.class);
      assertThat(retrievedUser).isNotNull();
      assertThat(retrievedUser.status()).isEqualTo(ActiveStatus.ONLINE);
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
}