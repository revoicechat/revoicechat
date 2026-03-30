package fr.revoicechat.live.voice.web;

import jakarta.ws.rs.core.MediaType;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

@QuarkusTest
class TestVoiceWebSocketDocResource {

  @Test
  void test() {
    var result = RestAssured.given().contentType(MediaType.APPLICATION_JSON)
                            .when().get("/ws/voice")
                            .then().statusCode(200)
                            .extract().body().asPrettyString();
    Assertions.assertThat(result).isEqualToNormalizingNewlines("""
                                                                   {
                                                                       "url": "ws://*url*/api/voice/{roomId}?token={jwtToken}"
                                                                   }""");
  }
}