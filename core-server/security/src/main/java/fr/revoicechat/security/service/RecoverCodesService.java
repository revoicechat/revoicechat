package fr.revoicechat.security.service;

import static fr.revoicechat.security.model.RecoverCodeStatus.*;
import static java.util.stream.Collectors.toSet;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.model.UserRecoverCode;
import fr.revoicechat.security.repository.UserRecoverCodeRepository;
import fr.revoicechat.security.utils.PasswordUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RecoverCodesService {

  private final EntityManager entityManager;
  private final UserRecoverCodeRepository userRecoverCodeRepository;
  private final SecureRandom secureRandom;

  public RecoverCodesService(
      EntityManager entityManager,
      UserRecoverCodeRepository userRecoverCodeRepository
  ) throws NoSuchAlgorithmException {
    this.entityManager = entityManager;
    this.userRecoverCodeRepository = userRecoverCodeRepository;
    this.secureRandom = SecureRandom.getInstanceStrong();
  }

  @Transactional
  public Set<String> generate(AuthenticatedUser user) {
    var oldCodes = userRecoverCodeRepository.findByUser(user.getId());
    revokeOldCodes(oldCodes);
    List<String> existingCodes = new ArrayList<>();
    oldCodes.stream().map(UserRecoverCode::getCode).forEach(existingCodes::add);
    return IntStream.range(0, 10)
                    .mapToObj(_ -> generate(user, existingCodes))
                    .collect(toSet());
  }

  @Transactional
  public boolean consume(AuthenticatedUser user, String code) {
    var validCode = userRecoverCodeRepository.findByUser(user.getId())
                                         .stream()
                                         .filter(recoverCode -> recoverCode.getStatus() == ACTIVE)
                                         .filter(recoverCode -> PasswordUtils.matches(code, recoverCode.getCode()))
                                         .findFirst();
    if (validCode.isEmpty()) {
      return false;
    }
    consume(validCode.get());
    return true;
  }

  private void consume(final UserRecoverCode code) {
    code.setStatus(CONSUMED);
    entityManager.persist(code);
  }

  private void revokeOldCodes(final List<UserRecoverCode> codes) {
    codes.stream().filter(code -> code.getStatus() == ACTIVE)
         .forEach(code -> {
           code.setStatus(REVOKED);
           entityManager.persist(code);
         });
  }

  private String generate(final AuthenticatedUser user, final List<String> existingCodes) {
    String plaintext;
    do {
      plaintext = generatePlaintext();
    } while (existingCodes.contains(plaintext));

    String hashed = PasswordUtils.encode(plaintext);

    var entity = new UserRecoverCode();
    entity.setUserId(user.getId());
    entity.setCode(hashed);
    entity.setStatus(ACTIVE);
    entity.setCreatedAt(LocalDateTime.now());
    entityManager.persist(entity);

    existingCodes.add(plaintext);
    return plaintext;
  }

  private String generatePlaintext() {
    byte[] bytes = new byte[10];
    secureRandom.nextBytes(bytes);
    return HexFormat.of().formatHex(bytes);
  }
}
