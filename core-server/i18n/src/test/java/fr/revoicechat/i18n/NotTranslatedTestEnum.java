package fr.revoicechat.i18n;

enum NotTranslatedTestEnum implements LocalizedMessage {
  TEST;

  @Override
  public String fileName() {
    return getClass().getCanonicalName();
  }

}
