package fr.revoicechat.security.service.totp;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HMACBasedOneTimePasswordGenerator {

  private static final int DIGITS = 6;
  private static final String ALGORITHM = "HmacSHA1";

  /** Core HOTP logic (RFC 4226) */
  String generate(byte[] secret, long counter) throws NoSuchAlgorithmException, InvalidKeyException {
    byte[] counterBytes = ByteBuffer.allocate(8).putLong(counter).array();
    byte[] hash = hmacSha1(secret, counterBytes);
    int binary = dynamicTruncation(hash);
    int otp = binary % (int) Math.pow(10, DIGITS);
    return String.format("%0" + DIGITS + "d", otp);
  }

  @SuppressWarnings("java:S4790") // Used algorithm in TOTP
  private byte[] hmacSha1(final byte[] secret, final byte[] counterBytes) throws NoSuchAlgorithmException, InvalidKeyException {
    Mac mac = Mac.getInstance(ALGORITHM);
    mac.init(new SecretKeySpec(secret, ALGORITHM));
    return mac.doFinal(counterBytes);
  }

  private int dynamicTruncation(final byte[] hash) {
    int offset = hash[hash.length - 1] & 0x0F;
    return ((hash[offset] & 0x7F) << 24)
           | ((hash[offset + 1] & 0xFF) << 16)
           | ((hash[offset + 2] & 0xFF) << 8)
           | (hash[offset + 3] & 0xFF);
  }
}
