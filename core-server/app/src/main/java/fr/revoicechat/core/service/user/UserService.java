package fr.revoicechat.core.service.user;

import static fr.revoicechat.core.model.InvitationType.APPLICATION_JOIN;
import static fr.revoicechat.core.nls.UserErrorCode.*;
import static java.util.function.Predicate.not;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.revoicechat.core.model.InvitationLink;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.model.UserType;
import fr.revoicechat.core.repository.UserRepository;
import fr.revoicechat.core.service.invitation.InvitationLinkUsage;
import fr.revoicechat.core.technicaldata.user.AdminUpdatableUserData;
import fr.revoicechat.core.technicaldata.user.NewUser;
import fr.revoicechat.core.technicaldata.user.NewUserSignup;
import fr.revoicechat.core.technicaldata.user.UpdatableUserData;
import fr.revoicechat.core.technicaldata.user.UpdatableUserData.PasswordUpdated;
import fr.revoicechat.risk.service.user.AuthenticatedUserEntityFinder;
import fr.revoicechat.security.UserHolder;
import fr.revoicechat.security.service.RecoverCodesService;
import fr.revoicechat.security.utils.PasswordUtils;
import fr.revoicechat.web.error.BadRequestException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class UserService implements AuthenticatedUserEntityFinder {

  private final EntityManager entityManager;
  private final UserRepository userRepository;
  private final UserHolder userHolder;
  private final PasswordValidation passwordValidation;
  private final InvitationLinkUsage invitationLinkUsage;
  private final RecoverCodesService recoverCodesService;
  private final boolean appOnlyAccessibleByInvitation;

  public UserService(EntityManager entityManager,
                     UserRepository userRepository,
                     UserHolder userHolder,
                     PasswordValidation passwordValidation,
                     InvitationLinkUsage invitationLinkUsage,
                     RecoverCodesService recoverCodesService,
                     @ConfigProperty(name = "revoicechat.global.app-only-accessible-by-invitation")
                     boolean appOnlyAccessibleByInvitation) {
    this.entityManager = entityManager;
    this.userRepository = userRepository;
    this.userHolder = userHolder;
    this.passwordValidation = passwordValidation;
    this.invitationLinkUsage = invitationLinkUsage;
    this.recoverCodesService = recoverCodesService;
    this.appOnlyAccessibleByInvitation = appOnlyAccessibleByInvitation;
  }

  @Transactional
  public NewUser create(final NewUserSignup signer) {
    if (signer.username() == null || signer.username().isEmpty()) {
      throw new BadRequestException(USER_LOGIN_INVALID);
    }
    passwordValidation.validate(signer.password());
    if (userRepository.count() == 0) {
      return generateUser(signer, null, UserType.ADMIN);
    }
    var invitationLink = Optional.ofNullable(signer.invitationLink())
                                 .map(id -> entityManager.find(InvitationLink.class, id))
                                 .orElse(null);
    if (appOnlyAccessibleByInvitation && !isValideInvitation(invitationLink)) {
      throw new BadRequestException(USER_WITH_NO_VALID_INVITATION);
    }
    return generateUser(signer, invitationLink, UserType.USER);
  }

  private NewUser generateUser(NewUserSignup signer, InvitationLink invitationLink, UserType type) {
    var user = new User();
    user.setId(UUID.randomUUID());
    user.setCreatedDate(OffsetDateTime.now());
    user.setDisplayName(signer.username());
    user.setLogin(signer.username());
    user.setEmail(signer.email());
    user.setType(type);
    user.setPassword(PasswordUtils.encode(signer.password()));
    entityManager.persist(user);
    invitationLinkUsage.use(invitationLink, user);
    return new NewUser(user, recoverCodesService.generate(user));
  }

  public User findByLogin(final String username) {
    return userRepository.findByLogin(username);
  }

  private static boolean isValideInvitation(final InvitationLink invitationLink) {
    return invitationLink != null
           && APPLICATION_JOIN.equals(invitationLink.getType())
           && invitationLink.isValid();
  }

  public User findCurrentUser() {
    return userHolder.get();
  }

  public List<User> fetchAll() {
    return entityManager.createQuery("select u from User u", User.class).getResultList();
  }

  @Transactional
  public List<User> fetchUserForServer(final UUID id) {
    return userRepository.findByServers(id).toList();
  }

  @Transactional
  public User updateAsAdmin(final UUID id, final AdminUpdatableUserData userData) {
    var user = getUser(id);
    Optional.ofNullable(userData.displayName()).filter(not(String::isBlank)).ifPresent(user::setDisplayName);
    Optional.ofNullable(userData.type()).ifPresent(user::setType);
    entityManager.persist(user);
    return user;
  }

  @Override
  @SuppressWarnings("unchecked")
  public User getUser(final UUID id) {
    return Optional.ofNullable(getUserOrNull(id)).orElseThrow(() -> new NotFoundException("User not found"));
  }

  public User getUserOrNull(final UUID id) {
    return entityManager.find(User.class, id);
  }

  @Transactional
  public User updateConnectedUser(final UpdatableUserData userData) {
    User user = userHolder.get();
    Optional.ofNullable(userData.password()).ifPresent(psw -> setPassword(user, psw));
    Optional.ofNullable(userData.displayName()).filter(not(String::isBlank)).ifPresent(user::setDisplayName);
    Optional.ofNullable(userData.status()).ifPresent(user::setStatus);
    entityManager.persist(user);
    return user;
  }

  public Stream<User> everyone() {
    return userRepository.everyone();
  }

  private void setPassword(final User user, final PasswordUpdated password) {
    if (!PasswordUtils.matches(password.password(), user.getPassword())) {
      throw new BadRequestException(USER_PASSWORD_WRONG);
    }
    if (Objects.equals(password.newPassword(), password.confirmPassword())) {
      user.setPassword(PasswordUtils.encode(password.newPassword()));
    } else {
      throw new BadRequestException(USER_PASSWORD_WRONG_CONFIRMATION);
    }
  }

  @Transactional
  public void forceSetPassword(final String password) {
    User user = userHolder.get();
    user.setPassword(PasswordUtils.encode(password));
    entityManager.persist(user);
  }
}
