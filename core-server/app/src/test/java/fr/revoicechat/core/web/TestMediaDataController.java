package fr.revoicechat.core.web;

import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.model.MediaDataStatus;
import fr.revoicechat.core.model.room.RoomType;
import fr.revoicechat.core.model.ServerType;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.representation.RoomRepresentation;
import fr.revoicechat.core.technicaldata.media.NewMediaData;
import fr.revoicechat.core.representation.MediaDataRepresentation;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.core.technicaldata.room.NewRoom;
import fr.revoicechat.core.representation.ServerRoomRepresentation;
import fr.revoicechat.core.technicaldata.server.NewServer;
import fr.revoicechat.core.representation.ServerRepresentation;
import fr.revoicechat.core.web.tests.RestTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@TestProfile(BasicIntegrationTestProfile.class)
@CleanDatabase
class TestMediaDataController {

  @Test
  @Transactional
  void testGetMedia() {
    String token = RestTestUtils.logNewUser();
    var server = createServer(token);
    var room = createRoom(token, server);
    var message = createMessage(token, room);
    var media = message.medias().getFirst();
    var result = RestAssured.given()
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .when().pathParam("id", media.id()).get("/media/{id}")
                            .then().statusCode(200)
                            .extract().body().as(MediaDataRepresentation.class);
    Assertions.assertThat(result).isEqualTo(media);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when().pathParam("id", UUID.randomUUID()).get("/media/{id}")
               .then().statusCode(404);
  }

  @Test
  void testUpdateMediaByStatus() {
    String token = RestTestUtils.logNewUser();
    var server = createServer(token);
    var room = createRoom(token, server);
    var message = createMessage(token, room);
    var media = message.medias().getFirst();
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when()
               .body("\"STORED\"")
               .pathParam("id", media.id()).patch("/media/{id}")
               .then().statusCode(200);
    var result = RestAssured.given()
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .when().pathParam("id", media.id()).get("/media/{id}")
                            .then().statusCode(200)
                            .extract().body().as(MediaDataRepresentation.class);
    Assertions.assertThat(result.status()).isEqualTo(MediaDataStatus.STORED);
  }

  @Test
  void testDelete() {
    String token = RestTestUtils.logNewUser();
    var server = createServer(token);
    var room = createRoom(token, server);
    var message = createMessage(token, room);
    var media = message.medias().getFirst();
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when()
               .pathParam("id", media.id()).delete("/media/{id}")
               .then().statusCode(200);
    var result = RestAssured.given()
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .when().pathParam("id", media.id()).get("/media/{id}")
                            .then().statusCode(200)
                            .extract().body().as(MediaDataRepresentation.class);
    Assertions.assertThat(result.status()).isEqualTo(MediaDataStatus.DELETING);
  }

  @Test
  void testGetMediaByStatus() {
    String token = RestTestUtils.logNewUser();
    var server = createServer(token);
    var room = createRoom(token, server);
    createMessage(token, room);
    createMessage(token, room);
    createMessage(token, room);
    createMessage(token, room);
    var result = RestAssured.given()
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .when().queryParam("status", MediaDataStatus.DOWNLOADING).get("/media")
                            .then().statusCode(200)
                            .extract().body().jsonPath().getList(".", MediaDataRepresentation.class);
    Assertions.assertThat(result).hasSize(4);
    result = RestAssured.given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .when().queryParam("status", MediaDataStatus.CORRUPT).get("/media")
                        .then().statusCode(200)
                        .extract().body().jsonPath().getList(".", MediaDataRepresentation.class);
    Assertions.assertThat(result).isEmpty();
  }

  private static MessageRepresentation createMessage(final String token, final RoomRepresentation room) {
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .body(new NewMessage("message 1", null, List.of(new NewMediaData("test1.png"))))
                      .when().pathParam("id", room.id()).put("/room/{id}/message")
                      .then().statusCode(200)
                      .extract().body().as(MessageRepresentation.class);
  }

  private static RoomRepresentation createRoom(final String token, final ServerRepresentation server) {
    NewRoom representation = new NewRoom("test", RoomType.TEXT);
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .body(representation)
                      .when().pathParam("id", server.id()).put("/server/{id}/room")
                      .then().statusCode(200)
                      .extract().body().as(ServerRoomRepresentation.class);
  }

  private static ServerRepresentation createServer(String token) {
    var representation = new NewServer("test", ServerType.PUBLIC);
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .body(representation)
                      .when().put("/server")
                      .then().statusCode(200)
                      .extract().as(ServerRepresentation.class);
  }
}
