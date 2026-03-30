package fr.revoicechat.core.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.technicaldata.emote.NewEmote;
import fr.revoicechat.core.representation.EmoteRepresentation;
import fr.revoicechat.core.representation.ServerRepresentation;
import fr.revoicechat.core.web.tests.RestTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;

/** @see fr.revoicechat.core.web.api.EmoteController */
@QuarkusTest
@TestProfile(BasicIntegrationTestProfile.class)
@CleanDatabase
class TestEmoteControllerImpl {

  @Test
  void testMyEmoteWithNoEmote() {
    var token = RestTestUtils.logNewUser("user");
    var result = getMyEmotes(token);
    assertThat(result).isEmpty();
  }

  @Test
  void testMyEmoteWithAdd() {
    var token = RestTestUtils.logNewUser("user");
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when()
               .body(new NewEmote("emote.png", "emote", List.of("keyword")))
               .put("/emote/me")
               .then().statusCode(200);
    var result = getMyEmotes(token);
    assertThat(result).hasSize(1);
    var emote = result.getFirst();
    assertThat(emote.keywords()).hasSize(1);
    assertThat(emote.name()).isEqualTo("emote");
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when()
               .pathParam("id", emote.id())
               .body(new NewEmote("emote.jpg", "emoote", List.of("keyword")))
               .patch("/emote/{id}")
               .then().statusCode(200);
    var resultUpdate = getMyEmotes(token);
    assertThat(resultUpdate).hasSize(1);
    var emoteUpdate = resultUpdate.getFirst();
    assertThat(emoteUpdate.keywords()).hasSize(1);
    assertThat(emoteUpdate.name()).isEqualTo("emoote");
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when()
               .pathParam("id", emote.id())
               .delete("/emote/{id}")
               .then().statusCode(204);
    var resultDelete = getMyEmotes(token);
    assertThat(resultDelete).isEmpty();
  }

  private static List<EmoteRepresentation> getMyEmotes(final String token) {
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .when().get("/emote/me")
                      .then().statusCode(200)
                      .extract().body().jsonPath().getList(".", EmoteRepresentation.class);
  }

  @Test
  void testServerEmotesWithNoEmote() {
    var token = RestTestUtils.logNewUser("user");
    var server = RestTestUtils.getServers(token).getFirst();
    var result = getServerEmotes(token, server);
    assertThat(result).isEmpty();
  }

  @Test
  void testServerEmotesWithAdd() {
    var token = RestTestUtils.logNewUser("user");
    var server = RestTestUtils.getServers(token).getFirst();
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when()
               .pathParam("id", server.id())
               .body(new NewEmote("emote.png", "emote", List.of("keyword")))
               .put("/emote/server/{id}")
               .then().statusCode(200);
    var result = getServerEmotes(token, server);
    assertThat(result).hasSize(1);
    var emote = result.getFirst();
    assertThat(emote.keywords()).hasSize(1);
    assertThat(emote.name()).isEqualTo("emote");
  }

  private static List<EmoteRepresentation> getServerEmotes(final String token, final ServerRepresentation server) {
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .when().pathParam("id", server.id()).get("/emote/server/{id}")
                      .then().statusCode(200)
                      .extract().body().jsonPath().getList(".", EmoteRepresentation.class);
  }

  @Test
  void testGlobalEmotesWithNoEmote() {
    var token = RestTestUtils.logNewUser("user");
    var result = getGlobalEmotes(token);
    assertThat(result).isEmpty();
  }

  @Test
  void testGlobalEmoteWithAdd() {
    var token = RestTestUtils.logNewUser("user");
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when()
               .body(new NewEmote("emote.png", "emote", List.of("keyword")))
               .put("/emote/global")
               .then().statusCode(200);
    var result = getGlobalEmotes(token);
    assertThat(result).hasSize(1);
    var emote = result.getFirst();
    assertThat(emote.keywords()).hasSize(1);
    assertThat(emote.name()).isEqualTo("emote");
  }

  private static List<EmoteRepresentation> getGlobalEmotes(final String token) {
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .when().get("/emote/global")
                      .then().statusCode(200)
                      .extract().body().jsonPath().getList(".", EmoteRepresentation.class);
  }
}