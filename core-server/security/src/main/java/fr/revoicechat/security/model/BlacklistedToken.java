package fr.revoicechat.security.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_BLACKLISTED_TOKEN")
public class BlacklistedToken {

  @Id private String token;
  private LocalDateTime expiresAt;

  public String getToken() {
    return token;
  }

  public void setToken(final String token) {
    this.token = token;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(final LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }
}
