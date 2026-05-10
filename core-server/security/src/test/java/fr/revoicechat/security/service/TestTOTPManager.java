package fr.revoicechat.security.service;

import static fr.revoicechat.security.service.TOTPManager.OTP_AUTH_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.model.TotpStatus;
import fr.revoicechat.security.model.UserType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@QuarkusTest
class TestTOTPManager {
  @Inject TOTPManager generator;
  @Inject EntityManager entityManager;

  @Test
  @Transactional
  void testGenerateTOTP() throws NotFoundException, IOException {
    // Given
    var user = newAuthenticatedUser();
    entityManager.persist(user);
    assumeThat(user.getTotpSecret()).isNull();
    assertThat(user.getTotpStatus()).isEqualTo(TotpStatus.INACTIVE);
    // When
    var qrCode = generator.generate(user.getId());
    // Then
    assertThat(qrCode).isNotNull();
    var result = entityManager.find(AuthenticatedUser.class, user.getId());
    assertThat(result.getTotpSecret()).isNotNull();
    assertThat(result.getTotpStatus()).isEqualTo(TotpStatus.ACTIVATION_PENDING);
    assertThat(decodeQRCode(qrCode.pgn()).getText())
        .isEqualTo(qrCode.url())
        .isEqualTo(OTP_AUTH_URL.formatted(user.getLogin(), user.getTotpSecret()));
  }

  private AuthenticatedUser newAuthenticatedUser() {
    var user = new AuthenticatedUser();
    user.setId(UUID.randomUUID());
    user.setDisplayName("test");
    user.setLogin("test");
    user.setType(UserType.USER);
    return user;
  }

  private static Result decodeQRCode(final byte[] qrCode) throws IOException, NotFoundException {
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(qrCode));
    LuminanceSource source = new BufferedImageLuminanceSource(image);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
    return new MultiFormatReader().decode(bitmap);
  }
}