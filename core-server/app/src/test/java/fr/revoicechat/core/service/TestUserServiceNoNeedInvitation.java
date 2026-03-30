package fr.revoicechat.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.model.InvitationLink;
import fr.revoicechat.core.model.InvitationLinkStatus;
import fr.revoicechat.core.model.InvitationType;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.model.UserType;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.service.user.UserService;
import fr.revoicechat.core.technicaldata.user.NewUserSignup;
import fr.revoicechat.core.service.TestUserServiceNoNeedInvitation.AppOnlyAccessibleByInvitationFalse;
import fr.revoicechat.security.utils.PasswordUtils;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@QuarkusTest
@CleanDatabase
@TestProfile(AppOnlyAccessibleByInvitationFalse.class)
class TestUserServiceNoNeedInvitation {

  @Inject EntityManager entityManager;
  @Inject UserService userService;

  @Test
  void testWithNoLink() {
    userService.create(new NewUserSignup("master", "psw", "master@revoicechat.fr", null));
    NewUserSignup signer = new NewUserSignup("user", "test", "user@revoicechat.fr", null);
    var resultRepresentation = userService.create(signer);
    assertThat(resultRepresentation).isNotNull();
    assertUser(resultRepresentation);
  }

  @Test
  void testWithRandomLink() {
    userService.create(new NewUserSignup("master", "psw", "master@revoicechat.fr", null));
    NewUserSignup signer = new NewUserSignup("user", "test", "user@revoicechat.fr", UUID.randomUUID());
    var resultRepresentation = userService.create(signer);
    assertThat(resultRepresentation).isNotNull();
    assertUser(resultRepresentation);
  }

  @Test
  @Transactional
  void testWithInvitationLink() {
    var adminRep = userService.create(new NewUserSignup("master", "psw", "master@revoicechat.fr", UUID.randomUUID()));
    var admin = entityManager.find(User.class, adminRep.getId());
    var invitation = generateInvitationLink(admin);
    NewUserSignup signer = new NewUserSignup("user", "test", "user@revoicechat.fr", invitation.getId());
    var resultRepresentation = userService.create(signer);
    assertThat(resultRepresentation).isNotNull();
    assertUser(resultRepresentation);
    invitation = entityManager.find(InvitationLink.class, invitation.getId());
    assertThat(invitation.getStatus()).isEqualTo(InvitationLinkStatus.USED);
    assertThat(invitation.getApplier()).isNotNull();
  }

  private void assertUser(final User resultRepresentation) {
    var result = entityManager.find(User.class, resultRepresentation.getId());
    assertThat(result).isNotNull();
    assertThat(result.getCreatedDate()).isNotNull();
    assertThat(result.getLogin()).isEqualTo("user");
    assertThat(result.getDisplayName()).isEqualTo("user");
    assertThat(result.getPassword()).matches(password -> PasswordUtils.matches("test", password));
    assertThat(result.getEmail()).isEqualTo("user@revoicechat.fr");
    assertThat(result.getType()).isEqualTo(UserType.USER);
  }

  private InvitationLink generateInvitationLink(final User admin) {
    var invitation = new InvitationLink();
    invitation.setId(UUID.randomUUID());
    invitation.setStatus(InvitationLinkStatus.CREATED);
    invitation.setType(InvitationType.APPLICATION_JOIN);
    invitation.setSender(admin);
    invitation.setTargetedServer(null);
    entityManager.persist(invitation);
    return invitation;
  }

  public static class AppOnlyAccessibleByInvitationFalse extends BasicIntegrationTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
      var map = new HashMap<>(super.getConfigOverrides());
      map.put("revoicechat.global.app-only-accessible-by-invitation", "false");
      return map;
    }
  }
}