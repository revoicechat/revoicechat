package fr.revoicechat.security.model.id;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UserRecoverCodeId implements Serializable {
  private UUID userId;
  private String code;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserRecoverCodeId that)) {
      return false;
    }
    return Objects.equals(userId, that.userId) &&
           Objects.equals(code, that.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, code);
  }
}