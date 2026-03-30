package fr.revoicechat.core.web;

import static fr.revoicechat.core.web.tests.RestTestUtils.signup;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.model.InvitationLinkStatus;
import fr.revoicechat.core.model.InvitationType;
import fr.revoicechat.core.model.ServerType;
import fr.revoicechat.core.model.server.ServerCategory;
import fr.revoicechat.core.model.server.ServerRoomItem;
import fr.revoicechat.core.model.server.ServerStructure;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.representation.InvitationRepresentation;
import fr.revoicechat.core.representation.RoomRepresentation;
import fr.revoicechat.core.representation.ServerRoomRepresentation;
import fr.revoicechat.core.technicaldata.server.NewServer;
import fr.revoicechat.core.representation.ServerRepresentation;
import fr.revoicechat.core.representation.UserRepresentation;
import fr.revoicechat.core.web.tests.RestTestUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@TestProfile(BasicIntegrationTestProfile.class)
@CleanDatabase
class TestServerController {

  @Test
  void creationPossible() {
    var user = RestTestUtils.signup("user", "psw");
    String token = RestTestUtils.login("user", "psw");
    var server = createServer(token);
    assertThat(server).isNotNull();
    assertThat(server.id()).isNotNull();
    assertThat(server.name()).isEqualTo("test");
    assertThat(server.owner()).isEqualTo(user.id());
  }

  @Test
  void testUpdateServer() {
    String token = RestTestUtils.logNewUser();
    createServer(token);
    var servers = getServers(token);
    ServerRepresentation server = servers.getFirst();
    assertThat(server.name()).isEqualTo("test");
    var newName = new NewServer("new name", ServerType.PUBLIC);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .body(newName)
               .when().pathParam("id", server.id()).patch("/server/{id}")
               .then().statusCode(200);
    server = RestAssured.given()
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .when().pathParam("id", server.id()).get("/server/{id}")
                        .then().statusCode(200)
                        .extract().as(ServerRepresentation.class);
    assertThat(server.name()).isEqualTo("new name");
  }

  @Test
  void testUpdateServerButResourceNotFound() {
    String token = RestTestUtils.logNewUser();
    var newName = new NewServer("new name", ServerType.PUBLIC);
    var randomId = UUID.randomUUID();
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .body(newName)
               .when().pathParam("id", randomId).patch("/server/{id}")
               .then().statusCode(401);
  }

  @Test
  void testGetServerWithNoServerForOneUser() {
    RestTestUtils.signup("user1", "psw");
    String tokenUser1 = RestTestUtils.login("user1", "psw");
    RestTestUtils.signup("user2", "psw");
    String tokenUser2 = RestTestUtils.login("user2", "psw");
    createServer(tokenUser1, "test1", ServerType.PUBLIC);
    createServer(tokenUser1, "test2", ServerType.PRIVATE);
    assertThat(getServers(tokenUser1)).hasSize(2);
    assertThat(getServers(tokenUser2)).isEmpty();
  }

  @Test
  void testDiscoverServer() {
    // Given
    RestTestUtils.signup("user1", "psw");
    String tokenUser1 = RestTestUtils.login("user1", "psw");
    var user2 = RestTestUtils.signup("user2", "psw");
    RestTestUtils.updateToAdmin(tokenUser1, user2);
    String tokenUser2 = RestTestUtils.login("user2", "psw");
    // When
    var serv1 = createServer(tokenUser1, "test1", ServerType.PUBLIC).id();
    createServer(tokenUser1, "test2", ServerType.PRIVATE);
    var serv3 = createServer(tokenUser2, "test3", ServerType.PUBLIC).id();
    createServer(tokenUser2, "test4", ServerType.PRIVATE);
    // Then
    assertThat(discoverServers(tokenUser1, false)).map(ServerRepresentation::id).containsExactlyInAnyOrder(serv3);
    assertThat(discoverServers(tokenUser2, false)).map(ServerRepresentation::id).containsExactlyInAnyOrder(serv1);
    assertThat(discoverServers(tokenUser1, true)).map(ServerRepresentation::id).containsExactlyInAnyOrder(serv1, serv3);
    assertThat(discoverServers(tokenUser2, true)).map(ServerRepresentation::id).containsExactlyInAnyOrder(serv1, serv3);
  }

  @Test
  void testJoinPublicServer() {
    // Given
    RestTestUtils.signup("user1", "psw");
    String tokenUser1 = RestTestUtils.login("user1", "psw");
    RestTestUtils.signup("user2", "psw");
    String tokenUser2 = RestTestUtils.login("user2", "psw");
    // When
    var serv1 = createServer(tokenUser1, "test1", ServerType.PUBLIC).id();
    var serv2 = createServer(tokenUser1, "test2", ServerType.PRIVATE).id();
    // Then
    assertThat(discoverServers(tokenUser2, false)).map(ServerRepresentation::id).contains(serv1).doesNotContain(serv2);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + tokenUser2)
               .when().pathParam("id", serv1).post("/server/{id}/join")
               .then().statusCode(204);
    assertThat(discoverServers(tokenUser2, false)).map(ServerRepresentation::id).isEmpty();
  }

  @Test
  void testJoinPublicServerButItsAPrivateServer() {
    // Given
    RestTestUtils.signup("user1", "psw");
    String tokenUser1 = RestTestUtils.login("user1", "psw");
    RestTestUtils.signup("user2", "psw");
    String tokenUser2 = RestTestUtils.login("user2", "psw");
    // When
    var serv2 = createServer(tokenUser1, "test2", ServerType.PRIVATE).id();
    // Then
    assertThat(discoverServers(tokenUser2, false)).map(ServerRepresentation::id).doesNotContain(serv2);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + tokenUser2)
               .when().pathParam("id", serv2).delete("/server/{id}/join")
               .then().statusCode(405);
  }

  @Test
  void testDeleteServer() {
    RestTestUtils.signup("user", "psw");
    String token = RestTestUtils.login("user", "psw");
    var server1 = createServer(token, "test1");
    var servers = getServers(token);
    assertThat(servers).hasSize(1);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when().pathParam("id", server1.id()).delete("/server/{id}")
               .then().statusCode(204);
    servers = getServers(token);
    assertThat(servers).isEmpty();
  }

  @Test
  void fetchUser() {
    String token = RestTestUtils.logNewUser();
    signup("Nyphew", "a");
    var server = createServer(token);
    var users = RestAssured.given()
                           .contentType(MediaType.APPLICATION_JSON)
                           .header("Authorization", "Bearer " + token)
                           .when().pathParam("id", server.id()).get("/server/{id}/user")
                           .then().statusCode(200)
                           .extract()
                           .body().jsonPath().getList(".", UserRepresentation.class);
    assertThat(users).hasSize(1);
  }

  @Test
  void testStructure() {
    String token = RestTestUtils.logNewUser();
    var server = createServer(token);
    var structure = RestAssured.given()
                               .contentType(MediaType.APPLICATION_JSON)
                               .header("Authorization", "Bearer " + token)
                               .when().pathParam("id", server.id()).get("/server/{id}/structure")
                               .then().statusCode(200)
                               .extract()
                               .body().as(ServerStructure.class);
    assertThat(structure.items()).hasSize(2);
    var item1 = (ServerCategory) structure.items().getFirst();
    assertThat(item1.name()).isEqualTo("text");
    assertThat(item1.items()).hasSize(2);
    var room1 = (ServerRoomItem) item1.items().getFirst();
    assertThat(getRoom(token, room1.id()).name()).isEqualTo("General");
    var room2 = (ServerRoomItem) item1.items().getLast();
    assertThat(getRoom(token, room2.id()).name()).isEqualTo("Random");
    assertThat(item1.items()).hasSize(2);
    var item2 = (ServerCategory) structure.items().getLast();
    assertThat(item2.name()).isEqualTo("vocal");
    assertThat(item2.items()).hasSize(1);
    var room3 = (ServerRoomItem) item2.items().getFirst();
    assertThat(getRoom(token, room3.id()).name()).isEqualTo("Vocal");
  }

  @Test
  void testUpdateEmptyStructure() {
    String token = RestTestUtils.logNewUser();
    var server = createServer(token);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .body(new ServerStructure(List.of()))
               .when().pathParam("id", server.id()).patch("/server/{id}/structure")
               .then().statusCode(200);
    var structure = RestAssured.given()
                               .contentType(MediaType.APPLICATION_JSON)
                               .header("Authorization", "Bearer " + token)
                               .when().pathParam("id", server.id()).get("/server/{id}/structure")
                               .then().statusCode(200)
                               .extract()
                               .body().as(ServerStructure.class);
    assertThat(structure.items()).isEmpty();
  }

  @Test
  void testUpdateStructure() {
    String token = RestTestUtils.logNewUser();
    var server = createServer(token);
    var structureBefore = RestAssured.given()
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .header("Authorization", "Bearer " + token)
                                     .when().pathParam("id", server.id()).get("/server/{id}/structure")
                                     .then().statusCode(200)
                                     .extract()
                                     .body().as(ServerStructure.class);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .body(new ServerStructure(List.of(structureBefore.items().getFirst())))
               .when().pathParam("id", server.id()).patch("/server/{id}/structure")
               .then().statusCode(200);
    var structure = RestAssured.given()
                               .contentType(MediaType.APPLICATION_JSON)
                               .header("Authorization", "Bearer " + token)
                               .when().pathParam("id", server.id()).get("/server/{id}/structure")
                               .then().statusCode(200)
                               .extract()
                               .body().as(ServerStructure.class);
    assertThat(structure.items()).hasSize(1);
  }

  @Test
  void testUpdateStructureWithInexistantRoomId() {
    String token = RestTestUtils.logNewUser();
    var server = createServer(token);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .body(new ServerStructure(List.of(new ServerRoomItem(UUID.randomUUID()))))
               .when().pathParam("id", server.id()).patch("/server/{id}/structure")
               .then().statusCode(400);
  }

  @Test
  void testRemoveStructure() {
    String token = RestTestUtils.logNewUser();
    var server = createServer(token);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when().pathParam("id", server.id()).patch("/server/{id}/structure")
               .then().statusCode(200);
    var structure = RestAssured.given()
                               .contentType(MediaType.APPLICATION_JSON)
                               .header("Authorization", "Bearer " + token)
                               .when().pathParam("id", server.id()).get("/server/{id}/structure")
                               .then().statusCode(200)
                               .extract()
                               .body().as(ServerStructure.class);
    assertThat(structure.items()).hasSize(3).allMatch(ServerRoomItem.class::isInstance);
  }

  @Test
  void testInvitationServer() {
    String token = RestTestUtils.logNewUser();
    var server = createServer(token);
    var invitation = RestAssured.given()
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .when().pathParam("id", server.id()).post("/server/{id}/invitation")
                                .then().statusCode(200)
                                .extract().body().as(InvitationRepresentation.class);
    assertThat(invitation.id()).isNotNull();
    assertThat(invitation.status()).isEqualTo(InvitationLinkStatus.CREATED);
    assertThat(invitation.type()).isEqualTo(InvitationType.SERVER_JOIN);
    assertThat(invitation.targetedServer()).isEqualTo(server.id());
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
                                    .when().pathParam("id", server.id()).get("/server/{id}/invitation")
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
    return createServer(token, "test");
  }

  private static ServerRepresentation createServer(String token, String name) {
    return createServer(token, name, ServerType.PUBLIC);
  }

  private static ServerRepresentation createServer(String token, String name, ServerType type) {
    var representation = new NewServer(name, type);
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .body(representation)
                      .when().put("/server")
                      .then().statusCode(200)
                      .extract().as(ServerRepresentation.class);
  }

  private static List<ServerRepresentation> getServers(String token) {
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .when().get("/server")
                      .then().statusCode(200)
                      .extract()
                      .body().jsonPath().getList(".", ServerRepresentation.class);
  }

  private static List<ServerRepresentation> discoverServers(String token, boolean joinedToo) {
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .when().queryParam("joinedToo", joinedToo).get("/server/discover")
                      .then().statusCode(200)
                      .extract()
                      .body().jsonPath().getList(".", ServerRepresentation.class);
  }

  private RoomRepresentation getRoom(String token, UUID id) {
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .when().pathParam("id", id).get("/room/{id}")
                      .then().statusCode(200)
                      .extract()
                      .body().as(ServerRoomRepresentation.class);
  }
}