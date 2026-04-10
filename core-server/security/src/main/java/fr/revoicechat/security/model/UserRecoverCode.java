package fr.revoicechat.security.model;

import java.time.LocalDateTime;
import java.util.UUID;

import fr.revoicechat.security.model.id.UserRecoverCodeId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "RVC_USER_RECOVER_CODES")
@IdClass(UserRecoverCodeId.class)
public class UserRecoverCode {
  @Id
  @Column(name = "USER_ID")
  private UUID userId;
  @Id
  @Column(name = "RECOVER_CODE")
  private String code;
  @Enumerated(EnumType.STRING)
  private RecoverCodeStatus status;
  private LocalDateTime createdAt;

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(final UUID userId) {
    this.userId = userId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(final String code) {
    this.code = code;
  }

  public RecoverCodeStatus getStatus() {
    return status;
  }

  public void setStatus(final RecoverCodeStatus status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(final LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
