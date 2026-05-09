package fr.revoicechat.core.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.representation.NewUserRepresentation;
import fr.revoicechat.core.technicaldata.user.NewUserSignup;
import fr.revoicechat.core.web.api.SignupController;
import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.security.representation.NewPassword;
import fr.revoicechat.security.representation.UserPassword;
import fr.revoicechat.security.representation.UserRecoveryCode;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

/** @see SignupController */
@QuarkusTest
@TestProfile(BasicIntegrationTestProfile.class)
@CleanDatabase
class TestAuthController {

  @Inject JWTParser jwtParser;

  @Test
  void testSignup() {
    var response = signup();
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    var newUser = response.as(NewUserRepresentation.class);
    assertThat(newUser).isNotNull();
    assertThat(newUser.user()).isNotNull();
    assertThat(newUser.recoverCodes()).hasSize(10);
    var user = newUser.user();
    assertThat(user.id()).isNotNull();
    assertThat(user.displayName()).isEqualTo("testUser");
    assertThat(user.login()).isEqualTo("testUser");
    assertThat(user.createdDate()).isNotNull();
    assertThat(user.status()).isEqualTo(ActiveStatus.OFFLINE);
  }

  @Test
  void testLogin() throws ParseException {
    var sign = signup();
    var user = sign.as(NewUserRepresentation.class);
    var response = login("testUser", "psw");
    assertThat(response.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
    var token = response.asString();
    assertThat(token).isNotNull();
    var jwt = jwtParser.parse(token);
    assertThat(jwt.getName()).isEqualTo(user.user().id().toString());
    assertThat(jwt.getSubject()).isEqualTo("testUser");
  }

  @Test
  void testLoginWrongPassword() {
    signup();
    var response = login("testUser", "pswwwww");
    assertThat(response.getStatusCode()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
    assertThat(response.asString()).isEqualTo("Invalid credentials");
  }

  @Test
  void testLoginWrongUsername() {
    signup();
    var response = login("not an existing username", "psw");
    assertThat(response.getStatusCode()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
    assertThat(response.asString()).isEqualTo("Invalid credentials");
  }

  @Test
  void testLogout() {
    signup();
    var response = login("testUser", "psw");
    var token = response.asString();
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when().get("/auth/logout")
               .then().statusCode(Status.OK.getStatusCode());
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when().get("/user/me")
               .then().statusCode(Status.UNAUTHORIZED.getStatusCode());
  }

  @Test
  void testWithNoConnectedUser() {
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .when().get("/auth/logout")
               .then().statusCode(Status.NO_CONTENT.getStatusCode());
  }

  @Test
  void testRegenerateRecoveryCode() {
    signup();
    var response = login("testUser", "psw");
    var token = response.asString();
    var codes = RestAssured.given()
                           .contentType(MediaType.APPLICATION_JSON)
                           .header("Authorization", "Bearer " + token)
                           .body(new UserPassword("testUser", "psw"))
                           .when().post("/auth/recovery-codes")
                           .then().statusCode(Status.OK.getStatusCode())
                           .extract().body().jsonPath().getList(".", String.class);
    assertThat(codes).hasSize(10);
  }

  @Test
  void testRegenerateTOTP() {
    signup();
    var response = login("testUser", "psw");
    var token = response.asString();
    var codes = RestAssured.given()
                           .contentType(MediaType.APPLICATION_JSON)
                           .header("Authorization", "Bearer " + token)
                           .body(new UserPassword("testUser", "psw"))
                           .when().post("/auth/totp-secret")
                           .then().statusCode(Status.OK.getStatusCode())
                           .extract().body().asByteArray();
    assertThat(codes).isNotEmpty();
  }

  @Test
  void testRecoveryCodeUsage() {
    var sign = signup();
    var user = sign.as(NewUserRepresentation.class);
    var token = RestAssured.given()
                           .contentType(MediaType.APPLICATION_JSON)
                           .body(new UserRecoveryCode("testUser", user.recoverCodes().iterator().next()))
                           .when().post("/auth/login/recovery-codes")
                           .then().statusCode(Status.OK.getStatusCode())
                           .extract().body().asString();
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .body(new NewPassword("newPsw", "newPsw"))
               .when().post("/auth/login/new-password")
               .then().statusCode(Status.OK.getStatusCode());
    login("testUser", "psw").then().statusCode(Status.UNAUTHORIZED.getStatusCode());
    login("testUser", "newPsw").then().statusCode(Status.OK.getStatusCode());
  }

  @Test
  void testRecoveryCodeUsageWrongPassword() {
    var sign = signup();
    var user = sign.as(NewUserRepresentation.class);
    var token = RestAssured.given()
                           .contentType(MediaType.APPLICATION_JSON)
                           .body(new UserRecoveryCode("testUser", user.recoverCodes().iterator().next()))
                           .when().post("/auth/login/recovery-codes")
                           .then().statusCode(Status.OK.getStatusCode())
                           .extract().body().asString();
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .body(new NewPassword("newPsw", "oldPsw"))
               .when().post("/auth/login/new-password")
               .then().statusCode(Status.BAD_REQUEST.getStatusCode());
  }

  @Test
  void testWrongRecoveryCodeUsage() {
    var sign = signup();
    var user = sign.as(NewUserRepresentation.class);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .body(new UserRecoveryCode("testUser", user.recoverCodes().iterator().next() + "wrong"))
               .when().post("/auth/login/recovery-codes")
               .then().statusCode(Status.UNAUTHORIZED.getStatusCode());
  }

  @Test
  void testInactiveRecoveryCodeUsage() {
    var sign = signup();
    var user = sign.as(NewUserRepresentation.class);
    var response = login("testUser", "psw");
    var token = response.asString();
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .body(new UserPassword("testUser", "psw"))
               .when().post("/auth/recovery-codes")
               .then().statusCode(Status.OK.getStatusCode());
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .body(new UserRecoveryCode("testUser", user.recoverCodes().iterator().next()))
               .when().post("/auth/login/recovery-codes")
               .then().statusCode(Status.UNAUTHORIZED.getStatusCode());
  }

  @Test
  void testRecoveryCodeUsageWrongLogin() {
    var sign = signup();
    var user = sign.as(NewUserRepresentation.class);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .body(new UserRecoveryCode("testUserWrong", user.recoverCodes().iterator().next()))
               .when().post("/auth/login/recovery-codes")
               .then().statusCode(Status.UNAUTHORIZED.getStatusCode());
  }


  private static Response signup() {
    var signup = new NewUserSignup("testUser", "psw", "email@email.fr", UUID.randomUUID());
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(signup)
                      .when().put("/auth/signup");
  }

  private static Response login(String userName, String password) {
    var wrongUserPassword = new UserPassword(userName, password);
    return RestAssured.given()
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(wrongUserPassword)
                      .when().post("/auth/login");
  }
}