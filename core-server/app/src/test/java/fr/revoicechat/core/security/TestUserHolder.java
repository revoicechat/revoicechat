package fr.revoicechat.core.security;

import java.util.UUID;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.model.UserType;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.security.UserHolder;
import fr.revoicechat.security.service.SecurityTokenService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;

@QuarkusTest
@CleanDatabase
@TestProfile(BasicIntegrationTestProfile.class)
class TestUserHolder {

  private static final String ID_USER = "35117c82-3b6f-403f-be5a-f3ee842d97d6";

  @Inject UserHolder userHolder;
  @Inject EntityManager entityManager;
  @Inject SecurityTokenService jwtService;

  @Test
  @TestSecurity(user = ID_USER)
  void testWithNoUserInDb() {
    Assertions.assertThatThrownBy(userHolder::get).isInstanceOf(NotFoundException.class);
  }

  @Test
  @Transactional
  @TestSecurity(user = ID_USER)
  void testWithUserInDb() {
    User user = new User();
    user.setId(UUID.fromString(ID_USER));
    user.setLogin("test-user");
    user.setDisplayName("test-user");
    user.setType(UserType.USER);
    entityManager.persist(user);
    User result = userHolder.get();
    Assertions.assertThat(result).isNotNull();
  }

  @Test
  @Transactional
  void testWithToken() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setLogin("test-user");
    user.setDisplayName("test-user");
    user.setType(UserType.USER);
    entityManager.persist(user);
    var token = jwtService.generate(user);
    Assertions.assertThat(userHolder.get(token)).isNotNull();
  }

  @Test
  @Transactional
  void testWithTokenAndNoUserInDb() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setLogin("test-user");
    user.setDisplayName("test-user");
    user.setType(UserType.USER);
    var token = jwtService.generate(user);
    Assertions.assertThat(userHolder.get(token)).isNull();
  }

  @Test
  @Transactional
  void testWithInvalidToken() {
    Assertions.assertThatThrownBy(() -> userHolder.get("not a token")).isInstanceOf(WebApplicationException.class);
  }
}