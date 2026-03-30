package fr.revoicechat.stub.voice.socket;

import static fr.revoicechat.core.model.room.RoomType.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.model.room.RoomType;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.model.UserType;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.technicaldata.room.NewRoom;
import fr.revoicechat.core.representation.ServerRoomRepresentation;
import fr.revoicechat.core.representation.ServerRepresentation;
import fr.revoicechat.core.web.tests.RestTestUtils;
import fr.revoicechat.live.voice.socket.VoiceWebSocket;
import fr.revoicechat.security.service.SecurityTokenService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@CleanDatabase
@TestProfile(BasicIntegrationTestProfile.class)
class TestVoiceWebSocket {

  @Inject SecurityTokenService jwtService;

  @Test
  void test() throws Exception {
    String user1 = RestTestUtils.logNewUser("user1");
    String user2 = RestTestUtils.logNewUser("user2");
    UUID room1 = createRoom(user1, "voice 1", VOICE);
    RestTestUtils.addAllRiskToAllUser(user1);
    try (var webSocketRoom1User1 = WebSocket.of(room1, user1);
         var webSocketRoom1User2 = WebSocket.of(room1, user2)) {
      await().atMost(20, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               webSocketRoom1User1.send("test");
               assertThat(webSocketRoom1User1.getMessage()).isNull();
               assertThat(webSocketRoom1User1.getByteMessage()).isNull();
               assertThat(webSocketRoom1User2.getMessage()).isNotNull();
               assertThat(webSocketRoom1User2.getByteMessage()).isNull();
             });
    }
  }

  @Test
  void testWithQueryParam() throws Exception {
    String user1 = RestTestUtils.logNewUser("userA");
    String user2 = RestTestUtils.logNewUser("userB");
    UUID room1 = createRoom(user1, "voice 1", VOICE);
    RestTestUtils.addAllRiskToAllUser(user1);
    try (var webSocketRoom1User1 = WebSocket.of("ws://localhost:8081/api/voice/" + room1 + "?token=" + user1);
         var webSocketRoom1User2 = WebSocket.of("ws://localhost:8081/api/voice/" + room1 + "?token=" + user2)) {
      await().atMost(20, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               webSocketRoom1User1.send("test");
               assertThat(webSocketRoom1User1.getMessage()).isNull();
               assertThat(webSocketRoom1User1.getByteMessage()).isNull();
               assertThat(webSocketRoom1User2.getMessage()).isNotNull();
               assertThat(webSocketRoom1User2.getByteMessage()).isNull();
             });
    }
  }

  @Test
  void testSendBytes() throws Exception {
    String user1 = RestTestUtils.logNewUser("user1");
    String user2 = RestTestUtils.logNewUser("user2");
    UUID room1 = createRoom(user1, "voice 1", VOICE);
    RestTestUtils.addAllRiskToAllUser(user1);
    try (var webSocketRoom1User1 = WebSocket.of(room1, user1);
         var webSocketRoom1User2 = WebSocket.of(room1, user2)) {
      await().atMost(20, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               webSocketRoom1User1.send(new byte[] { 1, 2 });
               assertThat(webSocketRoom1User1.getMessage()).isNull();
               assertThat(webSocketRoom1User1.getByteMessage()).isNull();
               assertThat(webSocketRoom1User2.getMessage()).isNull();
               assertThat(webSocketRoom1User2.getByteMessage()).isNotNull();
             });
    }
  }

  @Test
  void testWithNoToken() throws Exception {
    String user1 = RestTestUtils.logNewUser("user1");
    UUID room1 = createRoom(user1, "voice 1", VOICE);
    try (var webSocket = WebSocket.of("ws://localhost:8081/api/voice/" + room1)) {
      await().atMost(5, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               assertThat(webSocket.closeReason().get()).isNotNull();
               assertThat(webSocket.closeReason().get().getCloseCode()).isEqualTo(CloseCodes.VIOLATED_POLICY);
               assertThat(webSocket.closeReason().get().getReasonPhrase()).isEqualTo("Missing token");
             });
    }
  }

  @Test
  void testOnUserConnectedOnDifferentUser() throws Exception {
    String user1 = RestTestUtils.logNewUser("user1");
    String user2 = RestTestUtils.logNewUser("user2");
    UUID room1 = createRoom(user1, "voice 1", VOICE);
    UUID room2 = createRoom(user1, "voice 2", VOICE);
    try (var webSocketRoom1User1 = WebSocket.of(room1, user1);
         var webSocketRoom1User2 = WebSocket.of(room2, user2)) {
      await().atMost(20, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               webSocketRoom1User1.send("testOnUserConnectedOnDifferentUser");
               assertThat(webSocketRoom1User1.getMessage()).isNull();
               assertThat(webSocketRoom1User1.getByteMessage()).isNull();
               assertThat(webSocketRoom1User2.getMessage()).isNull();
               assertThat(webSocketRoom1User2.getByteMessage()).isNull();
             });
    }
  }

  @Test
  void testCloseOldSession() throws Exception {
    String user1 = RestTestUtils.logNewUser("user1");
    UUID room1 = createRoom(user1, "voice 1", VOICE);
    UUID room2 = createRoom(user1, "voice 2", VOICE);
    try (var webSocket1 = WebSocket.of(room1, user1);
         var webSocket2 = WebSocket.of(room2, user1)) {
      await().atMost(5, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               assertThat(webSocket1.closeReason().get()).isNotNull();
               assertThat(webSocket1.closeReason().get().getCloseCode()).isEqualTo(CloseCodes.NORMAL_CLOSURE);
               assertThat(webSocket1.closeReason().get().getReasonPhrase()).isEqualTo("Client disconnected");
               assertThat(webSocket2.closeReason().get()).isNull();
             });
    }
  }

  @Test
  void testOnRoomDoesNotExists() throws Exception {
    String user1 = RestTestUtils.logNewUser("user1");
    try (var webSocket = WebSocket.of(UUID.randomUUID(), user1)) {
      await().atMost(5, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               assertThat(webSocket.closeReason().get()).isNotNull();
               assertThat(webSocket.closeReason().get().getCloseCode()).isEqualTo(CloseCodes.CANNOT_ACCEPT);
               assertThat(webSocket.closeReason().get().getReasonPhrase()).isEqualTo("Selected room cannot accept websocket chat type");
             });
    }
  }

  @Test
  void testCloseOnWrongToken() throws Exception {
    String user1 = RestTestUtils.logNewUser("user1");
    UUID room1 = createRoom(user1, "voice 1", VOICE);
    try (var webSocket = WebSocket.of(room1, user1 + "wrongToken")) {
      await().atMost(5, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               assertThat(webSocket.closeReason().get()).isNotNull();
               assertThat(webSocket.closeReason().get().getCloseCode()).isEqualTo(CloseCodes.VIOLATED_POLICY);
               assertThat(webSocket.closeReason().get().getReasonPhrase()).startsWith("Invalid token: ");
             });
    }
  }

  @Test
  void testValidTokenButUserDoesNotExists() throws Exception {
    var token = fakeUser();
    String user1 = RestTestUtils.logNewUser("user1");
    UUID room1 = createRoom(user1, "voice 1", VOICE);
    try (var webSocket = WebSocket.of(room1, token)) {
      await().atMost(5, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               assertThat(webSocket.closeReason().get()).isNotNull();
               assertThat(webSocket.closeReason().get().getCloseCode()).isEqualTo(CloseCodes.VIOLATED_POLICY);
               assertThat(webSocket.closeReason().get().getReasonPhrase()).startsWith("Invalid token");
             });
    }
  }

  private String fakeUser() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setLogin("test-user");
    user.setDisplayName("test-user");
    user.setType(UserType.USER);
    return jwtService.generate(user);
  }

  @Test
  void testOnError() throws Exception {
    String user1 = RestTestUtils.logNewUser("user1");
    UUID room1 = createRoom(user1, "voice 1", VOICE);
    try (var webSocket = WebSocket.of(room1, user1 + "wrongToken")) {
      await().atMost(5, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               var chatWebSocket = CDI.current().select(VoiceWebSocket.class).get();
               var session = webSocket.session();
               var npe = new NullPointerException("NPE");
               assertThatCode(() -> chatWebSocket.onError(session, npe)).doesNotThrowAnyException();
             });
    }
  }

  @Test
  void testOnRoomNotVoice() throws Exception {
    String user1 = RestTestUtils.logNewUser("user1");
    UUID room1 = createRoom(user1, "text", TEXT);
    try (var webSocket = WebSocket.of(room1, user1)) {
      await().atMost(5, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               assertThat(webSocket.closeReason().get()).isNotNull();
               assertThat(webSocket.closeReason().get().getCloseCode()).isEqualTo(CloseCodes.CANNOT_ACCEPT);
               assertThat(webSocket.closeReason().get().getReasonPhrase()).isEqualTo("Selected room cannot accept websocket chat type");
             });
    }
  }

  private UUID createRoom(String token, String roomName, RoomType roomType) {
    var server = getServers(token).getFirst();
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .body(new NewRoom(roomName, roomType))
                      .when().pathParam("id", server.id()).put("/server/{id}/room")
                      .then().statusCode(200)
                      .extract().body()
                      .as(ServerRoomRepresentation.class).id();
  }

  private static List<ServerRepresentation> getServers(String token) {
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .when().get("/server")
                      .then().statusCode(200)
                      .extract()
                      .body()
                      .jsonPath().getList(".", ServerRepresentation.class);
  }
}