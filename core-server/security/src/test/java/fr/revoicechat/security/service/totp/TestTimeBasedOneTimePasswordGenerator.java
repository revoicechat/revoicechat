package fr.revoicechat.security.service.totp;

import static org.assertj.core.api.Assertions.*;

import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

import fr.revoicechat.security.error.AuthConfigException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class TestTimeBasedOneTimePasswordGenerator {

  @Inject TimeBasedOneTimePasswordGenerator timeBasedOneTimePasswordGenerator;

  @Test
  void testGenerateSecret() {
    assertThat(timeBasedOneTimePasswordGenerator.generateSecret())
              .isNotNull()
              .hasSize(20);
  }

  @Test
  void testGenerateTotp() {
    byte[] secret = "1234567890azertyuiop".getBytes();
    assertThat(timeBasedOneTimePasswordGenerator.generateTotp(secret)).hasSize(6);
  }

  @Test
  void testVerify() {
    byte[] secret = "1234567890azertyuiop".getBytes();
    var process = new TimeBasedOneTimePasswordGenerator(
        new HMACBasedOneTimePasswordGenerator() {
          @Override
          String generate(final byte[] secret, final long counter) {return "123456";}
        }
    );
    assertThat(process.verify(secret, "123456")).isTrue();
    assertThat(process.verify(secret, "000000")).isFalse();
  }

  @Test
  void testVerifyError() {
    byte[] secret = "1234567890azertyuiop".getBytes();
    var process = new TimeBasedOneTimePasswordGenerator(
        new HMACBasedOneTimePasswordGenerator() {
          @Override
          String generate(final byte[] secret, final long counter) throws NoSuchAlgorithmException {
            throw new NoSuchAlgorithmException();
          }
        }
    );
    assertThatThrownBy(() ->  process.verify(secret, "123456")).isInstanceOf(AuthConfigException.class);
  }

  @Test
  void testToBase32() {
    byte[] secret = "1234567890azertyuiop".getBytes();
    assertThat(timeBasedOneTimePasswordGenerator.toBase32(secret)).isEqualTo("GEZDGNBVGY3TQOJQMF5GK4TUPF2WS33Q");
  }
}