package fr.revoicechat.core.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;

@QuarkusTest
@TestProfile(BasicIntegrationTestProfile.class)
@CleanDatabase
class TestSettingsController {

  @Test
  void testGlobalSettings() {
    @SuppressWarnings("unchecked")
    Map<String, Object> settings = RestAssured.given()
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .when().get("/settings")
                                   .then().statusCode(200)
                                   .extract().body().as(Map.class);
    assertThat(settings).containsKeys(
        "global.app-only-accessible-by-invitation",
        "message.max-length"
    ).doesNotContainKeys("dev.error.log");
  }
}