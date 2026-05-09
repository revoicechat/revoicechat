package fr.revoicechat.security.service.qrcode;

import static org.assertj.core.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOError;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class TestBitMatrixQRCodeGenerator {
  @Inject BitMatrixQRCodeGenerator generator;

  @Test
  void testGenerateEmptyURI() {
    assertThatThrownBy(() -> generator.generate("")).isInstanceOf(IOError.class);
  }

  @Test
  void testGenerateForTotpQRCode() throws NotFoundException, IOException {
    // Given
    var uri = "otpauth://totp/test";
    // When
    var qrCode = generator.generate(uri);
    // Then
    assertThat(qrCode).isNotNull().isNotEmpty();
    byte[] pngSignature = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    assertThat(qrCode).startsWith(pngSignature);
    assertThat(decodeQRCode(qrCode).getText()).isEqualTo(uri);
  }

  private static Result decodeQRCode(final byte[] qrCode) throws IOException, NotFoundException {
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(qrCode));
    LuminanceSource source = new BufferedImageLuminanceSource(image);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
    return new MultiFormatReader().decode(bitmap);
  }
}