package fr.revoicechat.security.service;

import static fr.revoicechat.security.nls.UserErrorCode.TOTP_NOT_VALID;

import java.util.UUID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import fr.revoicechat.security.UserHolder;
import fr.revoicechat.security.error.AuthConfigException;
import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.model.TotpStatus;
import fr.revoicechat.security.service.qrcode.QRCodeGenerator;
import fr.revoicechat.security.service.totp.TimeBasedOneTimePasswordGenerator;

@ApplicationScoped
public class TOTPManager {

  static final String OTP_AUTH_URL = "otpauth://totp/RevoiceChat:%s?secret=%s&issuer=RevoiceChat&algorithm=SHA1&digits=6&period=30";

  private final UserHolder userHolder;
  private final EntityManager entityManager;
  private final QRCodeGenerator qrCodeGenerator;
  private final TimeBasedOneTimePasswordGenerator generator;

  public TOTPManager(UserHolder userHolder,
                     EntityManager entityManager,
                     QRCodeGenerator qrCodeGenerator,
                     TimeBasedOneTimePasswordGenerator generator) {
    this.userHolder = userHolder;
    this.entityManager = entityManager;
    this.qrCodeGenerator = qrCodeGenerator;
    this.generator = generator;
  }

  @Transactional
  public byte[] generate(UUID id) {
    var user = getUser(id);
    var base32Secret = generator.toBase32(generator.generateSecret());
    user.setTotpSecret(base32Secret);
    user.setTotpStatus(TotpStatus.ACTIVATION_PENDING);
    entityManager.persist(user);
    return qrCodeGenerator.generate(OTP_AUTH_URL.formatted(user.getLogin(), base32Secret));
  }

  @Transactional
  public boolean verify(final AuthenticatedUser user, final String code) {
    return user != null && generator.verify(generator.toBase32(user.getTotpSecret()), code);
  }

  @Transactional
  public void validate(final String secret) {
    var user = getUser(userHolder.getId());
    if (verify(user, secret)) {
      user.setTotpStatus(TotpStatus.ACTIVE);
      entityManager.persist(user);
    } else {
      throw new AuthConfigException(TOTP_NOT_VALID);
    }
  }

  private AuthenticatedUser getUser(final UUID id) {
    return entityManager.find(AuthenticatedUser.class, id);
  }
}
