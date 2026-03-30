package fr.revoicechat.risk.model;

public enum RiskMode {
  ENABLE,
  DISABLE,
  DEFAULT;

  public boolean isEnable() {
    return ENABLE.equals(this);
  }
}
