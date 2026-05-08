package fr.revoicechat.security.service.totp;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class TestHMACBasedOneTimePasswordGenerator {

  @Inject HMACBasedOneTimePasswordGenerator generator;

  @ParameterizedTest
  @CsvSource(textBlock = """
      1234567890azertyuiop |  100 | 106527
      1234567890azertyuiop |  102 | 291434
      1234567890azertyuiop | 1000 | 672538
      1234567890azertyuiop |  300 | 365721
      1234567890azertyuiop | 9999 | 907071
      WsE5D36A94Cfdeg23Asc |  100 | 125676
      WsE5D36A94Cfdeg23Asc |  102 | 685222
      WsE5D36A94Cfdeg23Asc | 1000 | 696040
      WsE5D36A94Cfdeg23Asc |  300 | 007194
      WsE5D36A94Cfdeg23Asc | 9999 | 534922
      """, delimiterString = "|")
  void test(String secret, long counter, String password) throws NoSuchAlgorithmException, InvalidKeyException {
    Assertions.assertThat(generator.generate(secret.getBytes(), counter)).isEqualTo(password);
  }
}