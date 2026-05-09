package fr.revoicechat.security.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import fr.revoicechat.security.model.BlacklistedToken;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@QuarkusTest
class TestTokenBlacklistService {
  @Inject TokenBlacklistService service;
  @Inject EntityManager entityManager;

  @Test
  void testBlacklistToken() {
    var token = "testBlacklistToken";
    service.blacklistToken(token, System.currentTimeMillis() + 99999);
    var entity = entityManager.find(BlacklistedToken.class, token);
    assertThat(entity).isNotNull();
    assertThat(entity.getToken()).isEqualTo(token);
    assertThat(entity.getExpiresAt()).isNotNull();
  }

  @Test
  void testExpiredToken() {
    var token = "testExpiredToken";
    service.blacklistToken(token, System.currentTimeMillis() - 99999);
    assertThat(service.isBlacklisted(token)).isTrue();
    service.cleanupExpiredTokens();
    assertThat(service.isBlacklisted(token)).isFalse();
  }

  @Test
  void testNotExpiredToken() {
    var token = "testNotExpiredToken";
    service.blacklistToken(token, System.currentTimeMillis() + 99999);
    assertThat(service.isBlacklisted(token)).isTrue();
    service.cleanupExpiredTokens();
    assertThat(service.isBlacklisted(token)).isTrue();
  }
}