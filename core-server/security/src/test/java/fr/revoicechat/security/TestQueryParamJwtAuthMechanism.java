package fr.revoicechat.security;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.security.model.AuthenticatedUser;
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
    var validJwt = jwtService.generate(new AuthenticatedUserMock());
    RestAssured.given()
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .when().get("/tests/secured-endpoint?jwt="+validJwt)
               .then()
               .statusCode(200);
  }

  private static class AuthenticatedUserMock implements AuthenticatedUser {

    @Override
    public UUID getId() {
      return UUID.fromString(ID_USER);
    }

    @Override
    public String getDisplayName() {
      return "user";
    }

    @Override
    public String getLogin() {
      return "user";
    }

    @Override
    public Set<String> getRoles() {
      return Set.of("USER");
    }
  }
}