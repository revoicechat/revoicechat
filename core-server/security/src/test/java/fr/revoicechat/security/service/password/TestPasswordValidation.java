package fr.revoicechat.security.service.password;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import fr.revoicechat.security.config.UserPasswordConfig;
import fr.revoicechat.web.error.BadRequestException;
import io.quarkus.test.junit.QuarkusTest;

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
      "Azerty123456",
  })
  void testInvalid(String password) {
    var config = new UserPasswordConfigMock();
    var service = new PasswordValidation(config);
    assertThatThrownBy(() -> service.validate(password))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("invalid password format");
  }

  @Test
  void testNull() {
    var config = new UserPasswordConfigMock();
    var service = new PasswordValidation(config);
    assertThatThrownBy(() -> service.validate(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("invalid password format");
  }

  @ParameterizedTest
  @ValueSource(strings = { "P@ss0rds" })
  void testValid(String password) {
    var config = new UserPasswordConfigMock();
    var service = new PasswordValidation(config);
    assertThatCode(() -> service.validate(password)).doesNotThrowAnyException();
  }

  static class UserPasswordConfigMock implements UserPasswordConfig {
    @Override public int minLength() {return 8;}
    @Override public int minUppercase() {return 1;}
    @Override public int minLowercase() {return 1;}
    @Override public int minNumber() {return 1;}
    @Override public int minSpecialChar() {return 1;}
  }
}