package fr.revoicechat.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.service.user.UserService;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.model.UserType;
import fr.revoicechat.core.technicaldata.user.NewUserSignup;
import fr.revoicechat.security.utils.PasswordUtils;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@CleanDatabase
@TestProfile(BasicIntegrationTestProfile.class)
class TestUserService {

  @Inject EntityManager entityManager;
  @Inject UserService userService;

  @Test
  void testGenerateAdmin() {
    // Given
    NewUserSignup signer = new NewUserSignup("master", "psw", "master@revoicechat.fr", null);
    var resultRepresentation = userService.create(signer);
    assertThat(resultRepresentation).isNotNull();
    var result = entityManager.find(User.class, resultRepresentation.getId());
    assertThat(result).isNotNull();
    assertThat(result.getCreatedDate()).isNotNull();
    assertThat(result.getLogin()).isEqualTo("master");
    assertThat(result.getDisplayName()).isEqualTo("master");
    assertThat(result.getPassword()).matches(password -> PasswordUtils.matches("psw", password));
    assertThat(result.getEmail()).isEqualTo("master@revoicechat.fr");
    assertThat(result.getType()).isEqualTo(UserType.ADMIN);
  }
}