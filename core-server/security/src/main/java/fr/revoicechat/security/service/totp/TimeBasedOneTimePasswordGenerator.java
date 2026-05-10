package fr.revoicechat.security.service.totp;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;

import org.apache.commons.codec.binary.Base32;

import fr.revoicechat.security.error.AuthConfigException;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TimeBasedOneTimePasswordGenerator {

  private static final int PERIOD = 30;

  private final Random random = new SecureRandom();
  private final Base32 base32 = new Base32();
  private final HMACBasedOneTimePasswordGenerator hmacBasedOneTimePasswordGenerator;

  public TimeBasedOneTimePasswordGenerator(HMACBasedOneTimePasswordGenerator hmacBasedOneTimePasswordGenerator) {
    this.hmacBasedOneTimePasswordGenerator = hmacBasedOneTimePasswordGenerator;
  }

  /** Generate a code for a given secret */
  public String generateTotp(byte[] secret) {
    long counter = Instant.now().getEpochSecond() / PERIOD;
    return generateHOTP(secret, counter);
  }

  /** Verify with clock-drift tolerance (±1 window) */
  public boolean verify(byte[] secret, String userCode) {
    long counter = Instant.now().getEpochSecond() / PERIOD;
    for (int i = -1; i <= 1; i++) {
      if (generateHOTP(secret, counter + i).equals(userCode)) {
        return true;
      }
    }
    return false;
  }

  /** Secret: generate & encode for storage */
  public byte[] generateSecret() {
    byte[] secret = new byte[20];
    random.nextBytes(secret);
    return secret;
  }

  /** Base 32 encoding is required by authenticator apps (Google Auth, Authy…) */
  public String toBase32(byte[] secret) {
    return base32.encodeToString(secret);
  }

  /** Base 32 encoding is required by authenticator apps (Google Auth, Authy…) */
  public byte[] toBase32(String secret) {
    return base32.decode(secret);
  }

  private String generateHOTP(final byte[] secret, final long counter) {
    try {
      return hmacBasedOneTimePasswordGenerator.generate(secret, counter);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new AuthConfigException(e);
    }
  }
}