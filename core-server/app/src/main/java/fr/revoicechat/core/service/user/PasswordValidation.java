package fr.revoicechat.core.service.user;

import static fr.revoicechat.core.nls.UserErrorCode.USER_PASSWORD_INVALID;

import fr.revoicechat.core.config.UserPasswordConfig;
import fr.revoicechat.web.error.BadRequestException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PasswordValidation {

  private final UserPasswordConfig userPasswordConfig;

  @Inject
  public PasswordValidation(final UserPasswordConfig userPasswordConfig) {
    this.userPasswordConfig = userPasswordConfig;
  }

  public void validate(String password) {
    if (password == null || password.length() < userPasswordConfig.minLength()) {
      throw new BadRequestException(USER_PASSWORD_INVALID);
    }
    if (!passwordStatistics(password).isValid(userPasswordConfig)) {
      throw new BadRequestException(USER_PASSWORD_INVALID);
    }
  }

  private static PasswordStatistics passwordStatistics(final String password) {
    int uppercase = 0;
    int lowercase = 0;
    int numbers = 0;
    int special = 0;

    for (char c : password.toCharArray()) {
      if (Character.isUpperCase(c)) {
        uppercase++;
      } else if (Character.isLowerCase(c)) {
        lowercase++;
      } else if (Character.isDigit(c)) {
        numbers++;
      } else {
        special++;
      }
    }
    return new PasswordStatistics(uppercase, lowercase, numbers, special);
  }

  private record PasswordStatistics(int uppercase, int lowercase, int numbers, int special) {

    boolean isValid(UserPasswordConfig userPasswordConfig) {
      return uppercase >= userPasswordConfig.minUppercase()
             && lowercase >= userPasswordConfig.minLowercase()
             && numbers >= userPasswordConfig.minNumber()
             && special >= userPasswordConfig.minSpecialChar();
    }
  }
}
