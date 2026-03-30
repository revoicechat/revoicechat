package fr.revoicechat.core.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.model.InvitationLink;
import fr.revoicechat.core.model.InvitationLinkStatus;
import fr.revoicechat.core.model.InvitationType;
import fr.revoicechat.core.model.ServerType;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.representation.InvitationRepresentation;
import fr.revoicechat.core.technicaldata.server.NewServer;
import fr.revoicechat.core.representation.ServerRepresentation;
import fr.revoicechat.core.web.tests.RestTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@TestProfile(BasicIntegrationTestProfile.class)
@CleanDatabase
class TestInvitationLinkController {

  @Inject EntityManager entityManager;

  @Test
  @Transactional
  void testGenerateApplicationInvitation() {
    var user = RestTestUtils.signup("user", "psw");
    String token = RestTestUtils.login("user", "psw");
    var invitationCreated = generateApplicationInvitation(token);
    assertThat(invitationCreated).isNotNull();
    assertThat(invitationCreated.id()).isNotNull();
    assertThat(invitationCreated.type()).isEqualTo(InvitationType.APPLICATION_JOIN);
    assertThat(invitationCreated.status()).isEqualTo(InvitationLinkStatus.CREATED);
    assertThat(invitationCreated.targetedServer()).isNull();
    var result = entityManager.find(InvitationLink.class, invitationCreated.id());
    assertThat(result).isNotNull();
    assertThat(result.getSender()).isNotNull();
    assertThat(result.getSender().getId()).isEqualTo(user.id());
    assertThat(result.getTargetedServer()).isNull();

  }

  private static InvitationRepresentation generateApplicationInvitation(final String token) {
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .when().post("/invitation/application")
                      .then().statusCode(200)
                      .extract().as(InvitationRepresentation.class);
  }

  @Test
  void testRevoke() {
    String token = RestTestUtils.logNewUser();
    var invitationCreated = generateApplicationInvitation(token);
    assertThat(invitationCreated.status()).isEqualTo(InvitationLinkStatus.CREATED);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when().pathParam("id", invitationCreated.id()).delete("/invitation/{id}")
               .then().statusCode(204);
    var invitationDeleted = RestAssured.given()
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .header("Authorization", "Bearer " + token)
                                       .when().pathParam("id", invitationCreated.id()).get("/invitation/{id}")
                                       .then().statusCode(200)
                                       .extract().as(InvitationRepresentation.class);
    assertThat(invitationDeleted.id()).isEqualTo(invitationCreated.id());
    assertThat(invitationDeleted.status()).isEqualTo(InvitationLinkStatus.REVOKED);
  }

  @Test
  void testRevokeWithNoExistenceDoesNotThrowAnyException() {
    String token = RestTestUtils.logNewUser();
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when().pathParam("id", UUID.randomUUID()).delete("/invitation/{id}")
               .then().statusCode(204);
  }

  @Test
  void testGetInvitationNoResource() {
    String token = RestTestUtils.logNewUser();
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when().pathParam("id", UUID.randomUUID()).get("/invitation/{id}")
               .then().statusCode(404);
  }

  @Test
  void testInvitationServer() {
    String token = RestTestUtils.logNewUser();
    var server = createServer(token);
    var invitation = RestAssured.given()
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .when().pathParam("id", server.id()).post("/invitation/server/{id}")
                                .then().statusCode(200)
                                .extract().body().as(InvitationRepresentation.class);
    assertThat(invitation.id()).isNotNull();
    assertThat(invitation.status()).isEqualTo(InvitationLinkStatus.CREATED);
    assertThat(invitation.type()).isEqualTo(InvitationType.SERVER_JOIN);
    assertThat(invitation.targetedServer()).isEqualTo(server.id());
  }

  @Test
  void testGetAppInvitation() {
    String tokenAdmin = RestTestUtils.logNewUser("admin");
    String tokenUser = RestTestUtils.logNewUser("user");
    var server = createServer(tokenAdmin);
    serverInvitation(tokenAdmin, server);
    applicationInvitation(tokenAdmin);
    applicationInvitation(tokenAdmin);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + tokenUser)
               .when().get("/invitation/application")
               .then().statusCode(403);
    var appInvitations = RestAssured.given()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + tokenAdmin)
                                    .when().get("/invitation/application")
                                    .then().statusCode(200)
                                    .extract().body().jsonPath().getList(".", InvitationRepresentation.class);
    assertThat(appInvitations).hasSize(2);
  }

  @Test
  void testGetServerInvitation() {
    String tokenAdmin = RestTestUtils.logNewUser("admin");
    var server = createServer(tokenAdmin);
    serverInvitation(tokenAdmin, server);
    applicationInvitation(tokenAdmin);
    applicationInvitation(tokenAdmin);
    var appInvitations = RestAssured.given()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Authorization", "Bearer " + tokenAdmin)
                                    .when().pathParam("id", server.id()).get("/invitation/server/{id}")
                                    .then().statusCode(200)
                                    .extract().body().jsonPath().getList(".", InvitationRepresentation.class);
    assertThat(appInvitations).hasSize(1);
  }

  private static void serverInvitation(final String tokenUser, final ServerRepresentation server) {
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + tokenUser)
               .when().pathParam("id", server.id()).post("/server/{id}/invitation")
               .then().statusCode(200);
  }

  private static void applicationInvitation(final String tokenUser) {
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + tokenUser)
               .when().post("/invitation/application")
               .then().statusCode(200);
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