package fr.revoicechat.i18n;

public interface RiskLocalizedMessage extends LocalizedMessage {

  @Override
  default String fileName() {
    return "risks";
  }
}
