package fr.revoicechat.security.service;

import static fr.revoicechat.security.model.RecoverCodeStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.revoicechat.security.model.AuthenticatedUser;
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
    var user = new AuthenticatedUserMock(UUID.randomUUID());
    // When
    var codes = recoverCodesService.generate(user);
    // Then
    assertThat(codes).hasSize(10);
    var allCodes = userRecoverCodeRepository.findByUser(user.getId());
    assertThat(allCodes).hasSize(10);
    assertThat(codes).allMatch(code -> allCodes.stream()
                                               .map(UserRecoverCode::getCode)
                                               .noneMatch(code::equals));
  }

  @Test
  void testRegenerate() {
    // Given
    var user = new AuthenticatedUserMock(UUID.randomUUID());
    recoverCodesService.generate(user);
    recoverCodesService.generate(user);
    // When
    var codes = recoverCodesService.generate(user);
    // Then
    var allCodes = userRecoverCodeRepository.findByUser(user.getId());
    assertThat(allCodes).hasSize(30);
    assertThat(codes).hasSize(10)
                     .allMatch(code -> allCodes.stream().map(UserRecoverCode::getCode).noneMatch(code::equals));
    var activeCodes = allCodes.stream().filter(code -> code.getStatus().equals(ACTIVE)).toList();
    var revokedCodes = allCodes.stream().filter(code -> code.getStatus().equals(REVOKED)).toList();
    assertThat(activeCodes).hasSize(10);
    assertThat(revokedCodes).hasSize(20);
  }

  public record AuthenticatedUserMock(UUID getId) implements AuthenticatedUser {

    @Override
    public String getDisplayName() {
      return "";
    }

    @Override
    public String getLogin() {
      return "";
    }

    @Override
    public Set<String> getRoles() {
      return Set.of();
    }
  }
}