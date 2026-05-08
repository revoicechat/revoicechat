package fr.revoicechat.security;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.model.UserType;
import fr.revoicechat.security.service.SecurityTokenService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
class TestQueryParamJwtAuthMechanism {

  private static final String ID_USER = "35117c82-3b6f-403f-be5a-f3ee842d97d6";

  @Inject SecurityTokenService jwtService;

  @Test
  void testWrongJwt() {
    RestAssured.given()
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .when().get("/tests/secured-endpoint?jwt=1234")
               .then()
               .statusCode(401);
  }

  @Test
  void testWrongNo() {
    RestAssured.given()
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .when().get("/tests/secured-endpoint")
               .then()
               .statusCode(401);
  }

  @Test
  void test() {
    var validJwt = jwtService.generate(newAuthenticatedUser());
    RestAssured.given()
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .when().get("/tests/secured-endpoint?jwt="+validJwt)
               .then()
               .statusCode(200);
  }

  private AuthenticatedUser newAuthenticatedUser() {
    var user = new AuthenticatedUser();
    user.setId(UUID.fromString(ID_USER));
    user.setDisplayName("user");
    user.setLogin("user");
    user.setPassword("");
    user.setType(UserType.USER);
    return user;
  }
}