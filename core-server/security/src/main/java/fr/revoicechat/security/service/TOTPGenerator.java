package fr.revoicechat.security.service;

import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.service.totp.TimeBasedOneTimePasswordGenerator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TOTPGenerator {

  private final EntityManager entityManager;
  private final TimeBasedOneTimePasswordGenerator generator;

  public TOTPGenerator(EntityManager entityManager, TimeBasedOneTimePasswordGenerator generator) {
    this.entityManager = entityManager;
    this.generator = generator;
  }

  @Transactional
  public byte[] generate(AuthenticatedUser user) {
    var base32Secret = generator.toBase32(generator.generateSecret());
    user.setBase32Secret(base32Secret);
    entityManager.persist(user);
    return generateQRCode(
        "otpauth://totp/RevoiceChat:%s?secret=%s&issuer=RevoiceChat&algorithm=SHA1&digits=6&period=30"
            .formatted(user.getId(), base32Secret)
    );
  }

  private byte[] generateQRCode(final String uri) {
    try {
      QRCodeWriter writer = new QRCodeWriter();
      BitMatrix bitMatrix = writer.encode(uri, BarcodeFormat.QR_CODE, 300, 300);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
      return outputStream.toByteArray();
    } catch (WriterException | IOException e) {
      throw new IOError(e);
    }
  }
}
