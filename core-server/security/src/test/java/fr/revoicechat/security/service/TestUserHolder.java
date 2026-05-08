package fr.revoicechat.security.service;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.revoicechat.security.UserHolder;
import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.model.UserType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

@QuarkusTest
class TestUserHolder {

  @Inject UserHolder userHolder;
  @Inject EntityManager entityManager;
  @Inject SecurityTokenService jwtService;

  @Test
  @Transactional
  void testWithToken() {
    AuthenticatedUser user = new AuthenticatedUser();
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
    AuthenticatedUser user = new AuthenticatedUser();
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