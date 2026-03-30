package fr.revoicechat.core.service;

import static fr.revoicechat.core.model.InvitationLinkStatus.*;
import static fr.revoicechat.core.model.InvitationType.APPLICATION_JOIN;
import static fr.revoicechat.core.nls.UserErrorCode.USER_WITH_NO_VALID_INVITATION;
import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

import fr.revoicechat.core.junit.CleanDatabase;
import fr.revoicechat.core.model.InvitationLink;
import fr.revoicechat.core.model.InvitationLinkStatus;
import fr.revoicechat.core.model.InvitationType;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.model.UserType;
import fr.revoicechat.core.quarkus.profile.BasicIntegrationTestProfile;
import fr.revoicechat.core.service.user.UserService;
import fr.revoicechat.core.technicaldata.user.NewUserSignup;
import fr.revoicechat.core.service.TestUserServiceNeedInvitation.AppOnlyAccessibleByInvitationTrue;
import fr.revoicechat.security.utils.PasswordUtils;
import fr.revoicechat.web.error.BadRequestException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@QuarkusTest
@CleanDatabase
@TestProfile(AppOnlyAccessibleByInvitationTrue.class)
class TestUserServiceNeedInvitation {

  @Inject EntityManager entityManager;
  @Inject UserService userService;

  @Test
  void testWithNoLink() {
    // Given
    userService.create(new NewUserSignup("master", "psw", "master@revoicechat.fr", null));
    NewUserSignup signer = new NewUserSignup("user", "psw", "user@revoicechat.fr", null);
    assertThatThrownBy(() -> userService.create(signer)).isInstanceOf(BadRequestException.class).hasMessage(USER_WITH_NO_VALID_INVITATION.translate());
  }

  @Test
  void testWithRandomLink() {
    userService.create(new NewUserSignup("master", "psw", "master@revoicechat.fr", null));
    NewUserSignup signer = new NewUserSignup("user", "test", "user@revoicechat.fr", UUID.randomUUID());
    assertThatThrownBy(() -> userService.create(signer)).isInstanceOf(BadRequestException.class).hasMessage(USER_WITH_NO_VALID_INVITATION.translate());
  }

  @Test
  @Transactional
  void testWithInvitationLink() {
    var adminRep = userService.create(new NewUserSignup("master", "psw", "master@revoicechat.fr", UUID.randomUUID()));
    var admin = entityManager.find(User.class, adminRep.getId());
    var invitation = generateInvitationLink(admin, CREATED, APPLICATION_JOIN);
    NewUserSignup signer = new NewUserSignup("user", "test", "user@revoicechat.fr", invitation.getId());
    var resultRepresentation = userService.create(signer);
    assertThat(resultRepresentation).isNotNull();
    assertUser(resultRepresentation);
    var user = entityManager.find(User.class, resultRepresentation.getId());
    entityManager.flush();
    entityManager.clear();
    var reloadedInvitation = entityManager.find(InvitationLink.class, invitation.getId());
    assertThat(reloadedInvitation.getStatus()).isEqualTo(USED);
    assertThat(reloadedInvitation.getApplier()).isEqualTo(user);
  }

  @Test
  @Transactional
  void testWithPermanentInvitationLink() {
    var adminRep = userService.create(new NewUserSignup("master", "psw", "master@revoicechat.fr", UUID.randomUUID()));
    var admin = entityManager.find(User.class, adminRep.getId());
    var invitation = generateInvitationLink(admin, PERMANENT, APPLICATION_JOIN);
    NewUserSignup signer = new NewUserSignup("user", "test", "user@revoicechat.fr", invitation.getId());
    var resultRepresentation = userService.create(signer);
    assertThat(resultRepresentation).isNotNull();
    assertUser(resultRepresentation);
    entityManager.flush();
    entityManager.clear();
    var reloadedInvitation = entityManager.find(InvitationLink.class, invitation.getId());
    assertThat(reloadedInvitation.getStatus()).isEqualTo(PERMANENT);
    assertThat(reloadedInvitation.getApplier()).isNull();
  }

  @ParameterizedTest
  @EnumSource(value = InvitationLinkStatus.class, names = { "CREATED", "PERMANENT" }, mode = Mode.EXCLUDE)
  @Transactional
  void testWithInvalidStatusInvitationLink(InvitationLinkStatus status) {
    var adminRep = userService.create(new NewUserSignup("master", "psw", "master@revoicechat.fr", UUID.randomUUID()));
    var admin = entityManager.find(User.class, adminRep.getId());
    var invitation = generateInvitationLink(admin, status, APPLICATION_JOIN);
    NewUserSignup signer = new NewUserSignup("user", "test", "user@revoicechat.fr", invitation.getId());
    assertThatThrownBy(() -> userService.create(signer)).isInstanceOf(BadRequestException.class).hasMessage(USER_WITH_NO_VALID_INVITATION.translate());
  }

  @ParameterizedTest
  @EnumSource(value = InvitationType.class, names = "APPLICATION_JOIN", mode = Mode.EXCLUDE)
  @Transactional
  void testWithInvalidTypeInvitationLink(InvitationType type) {
    var adminRep = userService.create(new NewUserSignup("master", "psw", "master@revoicechat.fr", UUID.randomUUID()));
    var admin = entityManager.find(User.class, adminRep.getId());
    var invitation = generateInvitationLink(admin, CREATED, type);
    NewUserSignup signer = new NewUserSignup("user", "test", "user@revoicechat.fr", invitation.getId());
    assertThatThrownBy(() -> userService.create(signer)).isInstanceOf(BadRequestException.class).hasMessage(USER_WITH_NO_VALID_INVITATION.translate());
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

  private InvitationLink generateInvitationLink(User admin, InvitationLinkStatus status, InvitationType type) {
    var invitation = new InvitationLink();
    invitation.setId(UUID.randomUUID());
    invitation.setStatus(status);
    invitation.setType(type);
    invitation.setSender(admin);
    entityManager.persist(invitation);
    return invitation;
  }

  public static class AppOnlyAccessibleByInvitationTrue extends BasicIntegrationTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
      var map = new HashMap<>(super.getConfigOverrides());
      map.put("revoicechat.global.app-only-accessible-by-invitation", "true");
      return map;
    }
  }
}