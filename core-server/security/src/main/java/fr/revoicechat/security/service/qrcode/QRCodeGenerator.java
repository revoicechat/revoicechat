package fr.revoicechat.security.service.qrcode;

/** Generate a QR code */
public interface QRCodeGenerator {
  byte[] generate(String uri);
}
