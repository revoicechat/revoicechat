package fr.revoicechat.i18n;

public interface LocalizedMessage {

  String name();

  String fileName();

  default String translate(Object... args) {
    return TranslationUtils.translate(this, args);
  }
}
