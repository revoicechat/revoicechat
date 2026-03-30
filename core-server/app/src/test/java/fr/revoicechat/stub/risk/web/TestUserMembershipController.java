package fr.revoicechat.stub.risk.web;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.model.ServerType;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.technicaldata.server.NewServer;
import fr.revoicechat.core.representation.ServerRepresentation;
import fr.revoicechat.core.web.tests.RestTestUtils;
import fr.revoicechat.risk.model.RiskMode;
import fr.revoicechat.risk.representation.CreatedServerRoleRepresentation;
import fr.revoicechat.risk.representation.RiskRepresentation;
import fr.revoicechat.risk.representation.ServerRoleRepresentation;
import fr.revoicechat.risk.type.RoleRiskType;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@CleanDatabase
@TestProfile(BasicIntegrationTestProfile.class)
class TestUserMembershipController {

  @ParameterizedTest
  @CsvSource(delimiterString = " -> ", textBlock = """
      ENABLE  -> 204
      DISABLE -> 401
      DEFAULT -> 401""")
  void test(RiskMode mode, int code) {
    var adminToken = RestTestUtils.logNewUser("admin");
    var user = RestTestUtils.signup("user", "psw");
    var userToken = RestTestUtils.login("user", "psw");
    var server = createServer(adminToken);
    Assertions.assertThat(getRoles(userToken)).isEmpty();
    Assertions.assertThat(getRoles(adminToken)).isEmpty();
    var role = RestAssured.given()
                          .contentType(MediaType.APPLICATION_JSON)
                          .header("Authorization", "Bearer " + adminToken)
                          .body(getCreatedServerRoleRepresentation(mode))
                          .when().pathParam("id", server.id()).put("/server/{id}/role")
                          .then().statusCode(200)
                          .extract().body().as(ServerRoleRepresentation.class);
    Assertions.assertThat(getRoles(userToken)).isEmpty();
    Assertions.assertThat(getRoles(adminToken)).isEmpty();
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + userToken)
               .body(List.of(user.id()))
               .when().pathParam("id", role.id()).put("/role/{id}/user")
               .then().statusCode(401);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + adminToken)
               .body(List.of(user.id()))
               .when().pathParam("id", role.id()).put("/role/{id}/user")
               .then().statusCode(204);
    Assertions.assertThat(getRoles(adminToken)).isEmpty();
    Assertions.assertThat(getRoles(userToken)).hasSize(1);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + userToken)
               .body(List.of(user.id()))
               .when().pathParam("id", role.id()).put("/role/{id}/user")
               .then().statusCode(code);
  }

  private static @NonNull CreatedServerRoleRepresentation getCreatedServerRoleRepresentation(RiskMode mode) {
    return new CreatedServerRoleRepresentation(
        "test",
        null,
        1,
        List.of(new RiskRepresentation(RoleRiskType.ADD_USER_ROLE, null, mode))
    );
  }

  private static List<ServerRoleRepresentation> getRoles(String token) {
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .when().get("/user/me/role")
                      .then().statusCode(200)
                      .extract().jsonPath().getList(".", ServerRoleRepresentation.class);
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