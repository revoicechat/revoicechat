package fr.revoicechat.stub.risk.web;

import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.model.ServerType;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.technicaldata.server.NewServer;
import fr.revoicechat.core.representation.ServerRepresentation;
import fr.revoicechat.core.risk.RoomRiskType;
import fr.revoicechat.core.web.tests.RestTestUtils;
import fr.revoicechat.risk.model.RiskMode;
import fr.revoicechat.risk.representation.CreatedServerRoleRepresentation;
import fr.revoicechat.risk.representation.RiskRepresentation;
import fr.revoicechat.risk.representation.ServerRoleRepresentation;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@CleanDatabase
@TestProfile(BasicIntegrationTestProfile.class)
class TestRoleController {

  @Test
  void test() {
    var adminToken = RestTestUtils.logNewUser("admin");
    var server = createServer(adminToken);
    CreatedServerRoleRepresentation serverRole = new CreatedServerRoleRepresentation(
        "test",
        null,
        1,
        List.of(new RiskRepresentation(RoomRiskType.SERVER_ROOM_READ_MESSAGE, null, RiskMode.ENABLE))
    );
    var createdRole = RestAssured.given()
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .header("Authorization", "Bearer " + adminToken)
                                 .body(serverRole)
                                 .when().pathParam("id", server.id()).put("/server/{id}/role")
                                 .then().statusCode(200)
                                 .extract().as(ServerRoleRepresentation.class);
    CreatedServerRoleRepresentation udpatedServerRole = new CreatedServerRoleRepresentation(
        "test1",
        "#FFFFFF",
        1,
        List.of(new RiskRepresentation(RoomRiskType.SERVER_ROOM_READ_MESSAGE, null, RiskMode.DISABLE))
    );
    var updatedRole = RestAssured.given()
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .header("Authorization", "Bearer " + adminToken)
                                 .body(udpatedServerRole)
                                 .when().pathParam("id", createdRole.id()).patch("/role/{id}")
                                 .then().statusCode(200)
                                 .extract().as(ServerRoleRepresentation.class);
    Assertions.assertThat(createdRole.id()).isEqualTo(updatedRole.id());
    Assertions.assertThat(createdRole.serverId()).isEqualTo(updatedRole.serverId());
    Assertions.assertThat(createdRole.color()).isNull();
    Assertions.assertThat(createdRole.name()).isEqualTo("test");
    Assertions.assertThat(updatedRole.color()).isNotNull();
    Assertions.assertThat(updatedRole.name()).isEqualTo("test1");
    var role = RestAssured.given()
                          .contentType(MediaType.APPLICATION_JSON)
                          .header("Authorization", "Bearer " + adminToken)
                          .when().pathParam("id", createdRole.id()).get("/role/{id}")
                          .then().statusCode(200)
                          .extract().as(ServerRoleRepresentation.class);
    Assertions.assertThat(role).isEqualTo(updatedRole);
  }

  @Test
  void testResourceNotExist() {
    var adminToken = RestTestUtils.logNewUser("admin");
    createServer(adminToken);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + adminToken)
               .when().pathParam("id", UUID.randomUUID()).get("/role/{id}")
               .then().statusCode(404);
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