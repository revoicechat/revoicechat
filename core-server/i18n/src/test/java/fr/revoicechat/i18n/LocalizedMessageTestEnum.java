package fr.revoicechat.i18n;

enum LocalizedMessageTestEnum implements LocalizedMessage {
  TEST_IN_ENGLISH_ONLY,
  TEST_IN_FRENCH_ONLY,
  TEST_IN_FRENCH_AND_ENGLISH,
  TEST_WITH_NO_TRANSLATION;

  @Override
  public String fileName() {
    return getClass().getCanonicalName();
  }

}
