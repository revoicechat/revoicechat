package fr.revoicechat.core.web.tests;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import fr.revoicechat.core.model.ServerType;
import fr.revoicechat.core.model.UserType;
import fr.revoicechat.core.technicaldata.login.UserPassword;
import fr.revoicechat.core.technicaldata.server.NewServer;
import fr.revoicechat.core.representation.ServerRepresentation;
import fr.revoicechat.core.technicaldata.user.AdminUpdatableUserData;
import fr.revoicechat.core.technicaldata.user.NewUserSignup;
import fr.revoicechat.core.representation.UserRepresentation;
import fr.revoicechat.risk.model.RiskMode;
import fr.revoicechat.risk.representation.CreatedServerRoleRepresentation;
import fr.revoicechat.risk.representation.RiskCategoryRepresentation;
import fr.revoicechat.risk.representation.RiskCategoryRepresentation.TranslatedRisk;
import fr.revoicechat.risk.representation.RiskRepresentation;
import fr.revoicechat.risk.representation.ServerRoleRepresentation;
import fr.revoicechat.risk.service.RiskCategoryService;
import fr.revoicechat.risk.type.RiskType;
import io.restassured.RestAssured;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.ws.rs.core.MediaType;

public class RestTestUtils {

  private static final AtomicInteger counter = new AtomicInteger(1);

  public static String logNewUser() {
    return logNewUser("user");
  }

  public static String logNewUser(String user) {
    signup(user, "psw");
    var token = login(user, "psw");
    joinServer(token);
    return token;
  }

  public static UserRepresentation signup(String user, String password) {
    var signup = new NewUserSignup(user, password, counter.getAndIncrement() + "@mail.com", UUID.randomUUID());
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(signup)
                      .when().put("/auth/signup")
                      .then().statusCode(200)
                      .extract().body().as(UserRepresentation.class);
  }

  public static void updateToAdmin(String admin, UserRepresentation userToUpdate) {
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + admin)
               .body(new AdminUpdatableUserData(userToUpdate.displayName(), UserType.ADMIN))
               .when().pathParam("id", userToUpdate.id()).patch("/user/{id}")
               .then().statusCode(200);
  }

  public static String login(String user, String password) {
    var login = new UserPassword(user, password);
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(login)
                      .when().post("/auth/login")
                      .then().statusCode(200)
                      .extract().body().asString();
  }

  private static void joinServer(final String token) {
    var servers = RestAssured.given()
                             .contentType(MediaType.APPLICATION_JSON)
                             .header("Authorization", "Bearer " + token)
                             .when().get("/server/discover")
                             .then().statusCode(200)
                             .extract().body().jsonPath().getList(".", ServerRepresentation.class);
    if (servers.isEmpty()) {
      var representation = new NewServer("test", ServerType.PUBLIC);
      RestAssured.given()
                 .contentType(MediaType.APPLICATION_JSON)
                 .header("Authorization", "Bearer " + token)
                 .body(representation)
                 .when().put("/server")
                 .then().statusCode(200);
    } else {
      RestAssured.given()
                 .contentType(MediaType.APPLICATION_JSON)
                 .header("Authorization", "Bearer " + token)
                 .when().pathParam("id", servers.getFirst().id()).post("/server/{id}/join")
                 .then().statusCode(204);
    }
  }

  public static void addAllRiskToAllUser(String tokenAdmin) {
    var allRisks = getAllRisks();
    getServers(tokenAdmin).forEach(server -> {
      var risks = allRisks.stream().map(type -> new RiskRepresentation(type, null, RiskMode.ENABLE)).toList();
      CreatedServerRoleRepresentation role = new CreatedServerRoleRepresentation("all risks", null, 1, risks);
      var newRole = RestAssured.given()
                               .contentType(MediaType.APPLICATION_JSON)
                               .header("Authorization", "Bearer " + tokenAdmin)
                               .when().pathParam("id", server.id()).body(role).put("/server/{id}/role")
                               .then().statusCode(200)
                               .extract().body().as(ServerRoleRepresentation.class);
      var users = getUser(tokenAdmin, server.id()).stream().map(UserRepresentation::id).toList();
      RestAssured.given()
                 .contentType(MediaType.APPLICATION_JSON)
                 .header("Authorization", "Bearer " + tokenAdmin)
                 .when().pathParam("id", newRole.id()).body(users).put("/role/{id}/user")
                 .then().statusCode(204);
    });
  }

  private static List<UserRepresentation> getUser(String token, UUID serverId) {
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .when().pathParam("id", serverId).get("/server/{id}/user")
                      .then().statusCode(200)
                      .extract()
                      .body().jsonPath().getList(".", UserRepresentation.class);
  }

  public static List<ServerRepresentation> getServers(String token) {
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + token)
                      .when().get("/server")
                      .then().statusCode(200)
                      .extract()
                      .body()
                      .jsonPath().getList(".", ServerRepresentation.class);
  }

  private static List<RiskType> getAllRisks() {
    return CDI.current().select(RiskCategoryService.class).get()
              .findAll().stream()
              .map(RiskCategoryRepresentation::risks)
              .flatMap(List::stream)
              .map(TranslatedRisk::type)
              .toList();
  }
}
