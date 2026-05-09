package fr.revoicechat.security.service.qrcode;

import java.io.ByteArrayOutputStream;
import java.io.IOError;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BitMatrixQRCodeGenerator implements QRCodeGenerator {

  @Override
  public byte[] generate(final String uri) {
    try {
      QRCodeWriter writer = new QRCodeWriter();
      BitMatrix bitMatrix = writer.encode(uri, BarcodeFormat.QR_CODE, 300, 300);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new IOError(e);
    }
  }
}
