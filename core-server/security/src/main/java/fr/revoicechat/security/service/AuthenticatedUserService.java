package fr.revoicechat.security.service;

import static fr.revoicechat.security.nls.UserErrorCode.USER_PASSWORD_WRONG_CONFIRMATION;

import java.util.Objects;

import fr.revoicechat.security.UserHolder;
import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.model.DefaultAuthenticatedUser;
import fr.revoicechat.security.repository.AuthenticatedUserRepository;
import fr.revoicechat.security.representation.NewPassword;
import fr.revoicechat.security.service.password.PasswordValidation;
import fr.revoicechat.security.utils.PasswordUtils;
import fr.revoicechat.web.error.BadRequestException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthenticatedUserService {

  private final UserHolder userHolder;
  private final EntityManager entityManager;
  private final AuthenticatedUserRepository userRepository;
  private final PasswordValidation passwordValidation;

  public AuthenticatedUserService(UserHolder userHolder,
                                  EntityManager entityManager,
                                  AuthenticatedUserRepository userRepository,
                                  PasswordValidation passwordValidation) {
    this.userHolder = userHolder;
    this.entityManager = entityManager;
    this.userRepository = userRepository;
    this.passwordValidation = passwordValidation;
  }

  public AuthenticatedUser findByLogin(String login) {
    return userRepository.findByLogin(login);
  }

  @Transactional
  public void forceSetPassword(final NewPassword password) {
    if (Objects.equals(password.password(), password.confirmPassword())) {
      passwordValidation.validate(password.password());
      var user = entityManager.find(DefaultAuthenticatedUser.class, userHolder.getId());
      user.setPassword(PasswordUtils.encode(password.password()));
      entityManager.persist(user);
    } else {
      throw new BadRequestException(USER_PASSWORD_WRONG_CONFIRMATION);
    }
  }
}
