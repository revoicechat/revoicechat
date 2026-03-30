package fr.revoicechat.web.mapper.error;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
class TestErrorThrowController {

  @Test
  void testError() {
    var result = RestAssured.given().contentType(MediaType.APPLICATION_JSON)
                            .when().get("/tests/error/throw")
                            .then().statusCode(500)
                            .extract().body().asPrettyString();
    Assertions.assertThat(result).contains("\"error\": \"Internal Server Error\"",
                                           "\"message\": \"Something went wrong on our side. Please try again later or contact support if the problem persists.\"",
                                           "\"errorFile\":",
                                           "\"swaggerDoc\": \"/api/q/swagger-ui\"");
  }

  @Test
  void testErrorInFrench() {
    var result = RestAssured.given()
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Accept-Language", "fr")
                            .when().get("/tests/error/throw")
                            .then().statusCode(500)
                            .extract().body().asPrettyString();
    Assertions.assertThat(result).contains("\"error\": \"Erreur interne du serveur\",",
                                           "\"message\": \"Problème est survenu de notre côté. Veuillez réessayer plus tard ou contacter l'assistance si le problème persiste.\",",
                                           "\"errorFile\": ",
                                           "\"swaggerDoc\": \"/api/q/swagger-ui\"");
  }
}