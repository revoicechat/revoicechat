package fr.revoicechat.moderation.model;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_SANCTION")
public class Sanction {
  @Id
  private UUID id;
  @Column(name = "TARGETED_USER_ID", nullable = false, updatable = false)
  private UUID targetedUser;
  @Column(name = "SERVER_ID", updatable = false)
  private UUID server;
  @Enumerated(EnumType.STRING)
  private SanctionType type;

  @Column(name = "START_AT")
  private LocalDateTime startAt;
  @Column(name = "EXPIRES_AT")
  private LocalDateTime expiresAt;

  @Column(name = "ISSUED_BY", nullable = false, updatable = false)
  private UUID issuedBy;
  private String reason;

  @Column(name = "REVOKED_BY")
  private UUID revokedBy;
  @Column(name = "REVOKED_AT")
  private LocalDateTime revokedAt;

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public UUID getTargetedUser() {
    return targetedUser;
  }

  public void setTargetedUser(final UUID targetedUser) {
    this.targetedUser = targetedUser;
  }

  public UUID getServer() {
    return server;
  }

  public void setServer(final UUID server) {
    this.server = server;
  }

  public SanctionType getType() {
    return type;
  }

  public void setType(final SanctionType type) {
    this.type = type;
  }

  public LocalDateTime getStartAt() {
    return startAt;
  }

  public void setStartAt(final LocalDateTime startAt) {
    this.startAt = startAt;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(final LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public UUID getIssuedBy() {
    return issuedBy;
  }

  public void setIssuedBy(final UUID issuedBy) {
    this.issuedBy = issuedBy;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(final String reason) {
    this.reason = reason;
  }

  public UUID getRevokedBy() {
    return revokedBy;
  }

  public void setRevokedBy(final UUID revokedBy) {
    this.revokedBy = revokedBy;
  }

  public LocalDateTime getRevokedAt() {
    return revokedAt;
  }

  public void setRevokedAt(final LocalDateTime revokedAt) {
    this.revokedAt = revokedAt;
  }

  public boolean isActive() {
    return !hasBeenRevoked() && !isExpired();
  }

  private boolean isExpired() {
    return getExpiresAt() != null && getExpiresAt().isBefore(LocalDateTime.now());
  }

  private boolean hasBeenRevoked() {
    return getRevokedBy() != null
           && getRevokedAt() != null
           && getRevokedAt().isBefore(LocalDateTime.now());
  }
}
