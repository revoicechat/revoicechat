package fr.revoicechat.security.model;

public enum TotpStatus {
  INACTIVE,
  ACTIVATION_PENDING,
  ACTIVE;

  public boolean active() {
    return equals(ACTIVE);
  }
}
