package fr.revoicechat.i18n;

public interface ErrorLocalizedMessage extends LocalizedMessage {

  @Override
  default String fileName() {
    return "error";
  }
}
