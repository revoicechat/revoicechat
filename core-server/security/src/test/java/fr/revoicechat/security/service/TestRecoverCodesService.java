package fr.revoicechat.security.service;

import static fr.revoicechat.security.model.RecoverCodeStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.security.model.UserRecoverCode;
import fr.revoicechat.security.repository.UserRecoverCodeRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class TestRecoverCodesService {

  @Inject RecoverCodesService recoverCodesService;
  @Inject UserRecoverCodeRepository userRecoverCodeRepository;

  @Test
  void test() {
    // Given
    var id = UUID.randomUUID();
    // When
    var codes = recoverCodesService.generate(id);
    // Then
    assertThat(codes).hasSize(10);
    var allCodes = userRecoverCodeRepository.findByUser(id);
    assertThat(allCodes).hasSize(10);
    assertThat(codes).allMatch(code -> allCodes.stream()
                                               .map(UserRecoverCode::getCode)
                                               .noneMatch(code::equals));
  }

  @Test
  void testRegenerate() {
    // Given
    var id = UUID.randomUUID();
    recoverCodesService.generate(id);
    recoverCodesService.generate(id);
    // When
    var codes = recoverCodesService.generate(id);
    // Then
    var allCodes = userRecoverCodeRepository.findByUser(id);
    assertThat(allCodes).hasSize(30);
    assertThat(codes).hasSize(10)
                     .allMatch(code -> allCodes.stream().map(UserRecoverCode::getCode).noneMatch(code::equals));
    var activeCodes = allCodes.stream().filter(code -> code.getStatus().equals(ACTIVE)).toList();
    var revokedCodes = allCodes.stream().filter(code -> code.getStatus().equals(REVOKED)).toList();
    assertThat(activeCodes).hasSize(10);
    assertThat(revokedCodes).hasSize(20);
  }
}