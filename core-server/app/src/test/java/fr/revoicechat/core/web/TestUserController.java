package fr.revoicechat.core.web;

import static fr.revoicechat.core.nls.UserErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.representation.UserRepresentation;
import fr.revoicechat.core.technicaldata.user.AdminUpdatableUserData;
import fr.revoicechat.core.technicaldata.user.UpdatableUserData;
import fr.revoicechat.core.technicaldata.user.UpdatableUserData.PasswordUpdated;
import fr.revoicechat.core.web.tests.RestTestUtils;
import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.web.mapper.error.ErrorResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@TestProfile(BasicIntegrationTestProfile.class)
@CleanDatabase
class TestUserController {

  @Test
  void testGetUser() {
    var signedUser = RestTestUtils.signup("nyphew", "psw");
    var token = RestTestUtils.login("nyphew", "psw");
    var retrievedUser = RestAssured.given()
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .header("Authorization", "Bearer " + token)
                                   .when().pathParam("id", signedUser.id()).get("/user/{id}")
                                   .then().statusCode(200)
                                   .extract().body().as(UserRepresentation.class);
    assertThat(retrievedUser).usingRecursiveComparison()
                             .withComparatorForType(Comparator.comparing(a -> a.truncatedTo(ChronoUnit.MILLIS)), OffsetDateTime.class)
                             .isEqualTo(signedUser);
  }

  @Test
  void testMe() {
    RestTestUtils.signup("rex_woof", "psw1");
    var signedUser = RestTestUtils.signup("nyphew", "psw2");
    var token = RestTestUtils.login("nyphew", "psw2");
    var retrievedUser = RestAssured.given()
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .header("Authorization", "Bearer " + token)
                                   .when().get("/user/me")
                                   .then().statusCode(200)
                                   .extract().body().as(UserRepresentation.class);
    assertThat(retrievedUser).usingRecursiveComparison()
                             .withComparatorForType(Comparator.comparing(a -> a.truncatedTo(ChronoUnit.MILLIS)), OffsetDateTime.class)
                             .isEqualTo(signedUser);
  }

  @Test
  void testUpdateMe() {
    var signedUser = RestTestUtils.signup("nyphew", "psw2");
    var token = RestTestUtils.login("nyphew", "psw2");
    UpdatableUserData userData = new UpdatableUserData("new_nyphew", null, ActiveStatus.OFFLINE);
    var updatedUser = RestAssured.given()
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .header("Authorization", "Bearer " + token)
                                 .when().body(userData).patch("/user/me")
                                 .then().statusCode(200)
                                 .extract().body().as(UserRepresentation.class);
    assertThat(updatedUser.id()).isEqualTo(signedUser.id());
    assertThat(updatedUser.displayName()).isEqualTo("new_nyphew");
  }

  @Test
  void testUpdateMePassword() {
    RestTestUtils.signup("nyphew", "psw");
    var token = RestTestUtils.login("nyphew", "psw");
    UpdatableUserData userData = new UpdatableUserData(null, new PasswordUpdated("psw", "new_psw", "new_psw"), null);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + token)
               .when().body(userData).patch("/user/me")
               .then().statusCode(200);
  }

  @Test
  void testUpdateMePasswordWithWrongPassword() {
    RestTestUtils.signup("nyphew", "psw");
    var token = RestTestUtils.login("nyphew", "psw");
    UpdatableUserData userData = new UpdatableUserData(null, new PasswordUpdated("wrongPsw", "new_psw", "new_psw"), null);
    var response = RestAssured.given()
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("Authorization", "Bearer " + token)
                              .when().body(userData).patch("/user/me")
                              .then().statusCode(400)
                              .extract().body().as(ErrorResponse.class);
    assertThat(response).isEqualTo(new ErrorResponse(USER_PASSWORD_WRONG.translate()));
  }

  @Test
  void testUpdateMePasswordWithWrongConfirmPassword() {
    RestTestUtils.signup("nyphew", "psw");
    var token = RestTestUtils.login("nyphew", "psw");
    UpdatableUserData userData = new UpdatableUserData(null, new PasswordUpdated("psw", "new_psw", "wrong_new_psw"), null);
    var response = RestAssured.given()
                              .contentType(MediaType.APPLICATION_JSON)
                              .header("Authorization", "Bearer " + token)
                              .when().body(userData).patch("/user/me")
                              .then().statusCode(400)
                              .extract().body().as(ErrorResponse.class);
    assertThat(response).isEqualTo(new ErrorResponse(USER_PASSWORD_WRONG_CONFIRMATION.translate()));
  }

  @Test
  void testUpdateUserCalledByAdminUser() {
    RestTestUtils.signup("admin", "psw");
    var tokenAdmin = RestTestUtils.login("admin", "psw");
    var user = RestTestUtils.signup("user", "psw2");
    AdminUpdatableUserData userData = new AdminUpdatableUserData("userName", null);
    var updatedUser = RestAssured.given()
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .header("Authorization", "Bearer " + tokenAdmin)
                                 .when().body(userData).pathParam("id", user.id()).patch("/user/{id}")
                                 .then().statusCode(200)
                                 .extract().body().as(UserRepresentation.class);
    assertThat(updatedUser.id()).isEqualTo(user.id());
    assertThat(updatedUser.displayName()).isEqualTo("userName");
  }

  @Test
  void testUpdateUserCalledBySimpleUser() {
    RestTestUtils.signup("admin", "psw");
    var user = RestTestUtils.signup("user", "psw2");
    var tokenUser = RestTestUtils.login("user", "psw2");
    AdminUpdatableUserData userData = new AdminUpdatableUserData("userName", null);
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization", "Bearer " + tokenUser)
               .when().body(userData).pathParam("id", user.id()).patch("/user/{id}")
               .then().statusCode(403);
  }

  @Test
  @TestSecurity(user = "b2b4f3e1-c15c-4e67-8657-77ee38b0b268", roles = { "USER" })
  void testGetUserNotFound() {
    RestAssured.given()
               .contentType(MediaType.APPLICATION_JSON)
               .when().pathParam("id", UUID.randomUUID()).get("/user/{id}")
               .then().statusCode(404);
  }
}