package fr.revoicechat.security.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import fr.revoicechat.security.model.BlacklistedToken;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TokenBlacklistService {

  private final EntityManager entityManager;

  public TokenBlacklistService(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Transactional
  public void blacklistToken(String token, long expiration) {
    var blacklistedToken = new BlacklistedToken();
    blacklistedToken.setToken(token);
    blacklistedToken.setExpiresAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(expiration), ZoneId.systemDefault()));
    entityManager.persist(blacklistedToken);
  }

  public boolean isBlacklisted(String token) {
    return entityManager.createQuery("SELECT COUNT(b) FROM BlacklistedToken b WHERE b.token = :token", Long.class)
                        .setParameter("token", token)
                        .getSingleResult() > 0;
  }

  /** Run hourly */
  @Scheduled(cron = "0 0 * * * ?")
  @Transactional
  public void cleanupExpiredTokens() {
    entityManager.createQuery("DELETE FROM BlacklistedToken b WHERE b.expiresAt < :now")
                 .setParameter("now", LocalDateTime.now())
                 .executeUpdate();
  }
}