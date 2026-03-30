package fr.revoicechat.core.service.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import fr.revoicechat.core.config.UserPasswordConfig;
import fr.revoicechat.web.error.BadRequestException;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Tests associés à {@link PasswordValidation}.
 */
@QuarkusTest
class TestPasswordValidation {

  @ParameterizedTest
  @ValueSource(strings = {
      "",
      "test",
      "passords",
      "PASSORDS",
      "P@ssords",
      "P@ss0rd",
      "12345678",
      "AAAAAAAA",
  })
  void testInvalid(String password) {
    var config = new UserPasswordConfigMock(8, 1, 1, 1, 1);
    var service = new PasswordValidation(config);
    assertThatThrownBy(() -> service.validate(password))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("invalid password format");
  }

  @Test
  void testNull() {
    var config = new UserPasswordConfigMock(8, 1, 1, 1, 1);
    var service = new PasswordValidation(config);
    assertThatThrownBy(() -> service.validate(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("invalid password format");
  }

  @ParameterizedTest
  @ValueSource(strings = { "P@ss0rds" })
  void testValid(String password) {
    var config = new UserPasswordConfigMock(8, 1, 1, 1, 1);
    var service = new PasswordValidation(config);
    assertThatCode(() -> service.validate(password)).doesNotThrowAnyException();
  }

  record UserPasswordConfigMock(int minLength,
                                int minUppercase,
                                int minLowercase,
                                int minNumber,
                                int minSpecialChar) implements UserPasswordConfig {}
}