package fr.revoicechat.stub.voice.socket;

import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.model.room.RoomType;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.technicaldata.room.NewRoom;
import fr.revoicechat.core.representation.RoomPresenceRepresentation;
import fr.revoicechat.core.representation.ServerRoomRepresentation;
import fr.revoicechat.core.representation.ServerRepresentation;
import fr.revoicechat.core.web.tests.RestTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@CleanDatabase
@TestProfile(BasicIntegrationTestProfile.class)
class TestServerRoomPresenceService {

  @Test
  void test() throws Exception {
    String user1 = RestTestUtils.logNewUser("user1");
    RestTestUtils.logNewUser("user2");
    UUID room = createRoom(user1);
    RestTestUtils.addAllRiskToAllUser(user1);
    try (var _ = WebSocket.of(room, user1)) {
      await().atMost(20, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               var presence = RestAssured.given()
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .header("Authorization", "Bearer " + user1)
                                         .when().pathParam("id", room).get("/room/{id}/user")
                                         .then().statusCode(200)
                                         .extract().body()
                                         .as(RoomPresenceRepresentation.class);
               Assertions.assertThat(presence.allUser()).hasSize(2)
                   .anyMatch(user -> user.login().equals("user1"))
                   .anyMatch(user -> user.login().equals("user2"));
               Assertions.assertThat(presence.connectedUser()).hasSize(1)
                         .anyMatch(user -> user.user().login().equals("user1"));
             });
    }
  }

  @Test
  void testMultiConnection() throws Exception {
    String user1 = RestTestUtils.logNewUser("user1");
    String user2 = RestTestUtils.logNewUser("user2");
    UUID room = createRoom(user1);
    RestTestUtils.addAllRiskToAllUser(user1);
    try (var _ = WebSocket.of(room, user1); var _ = WebSocket.of(room, user2)) {
      await().atMost(20, TimeUnit.SECONDS)
             .untilAsserted(() -> {
               var presence = RestAssured.given()
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .header("Authorization", "Bearer " + user1)
                                         .when().pathParam("id", room).get("/room/{id}/user")
                                         .then().statusCode(200)
                                         .extract().body()
                                         .as(RoomPresenceRepresentation.class);
               Assertions.assertThat(presence.allUser()).hasSize(2)
                         .anyMatch(user -> user.login().equals("user1"))
                         .anyMatch(user -> user.login().equals("user2"));
               Assertions.assertThat(presence.connectedUser()).hasSize(2)
                         .anyMatch(user -> user.user().login().equals("user1"))
                         .anyMatch(user -> user.user().login().equals("user2"));
             });
    }
  }

  private UUID createRoom(String token) {
    var server = getServers(token).getFirst();
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .body(new NewRoom("voice 1", RoomType.VOICE))
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
