package fr.revoicechat.i18n;

import static fr.revoicechat.i18n.LocalizedMessageTestEnum.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestLocalizedMessage {

  @BeforeEach
  void setUp() {
    CurrentLocaleHolder.removeLocale();
  }

  @Test
  void testEnglishOnlyMessageWithNoDefaultLocale() {
    assertThat(TEST_IN_ENGLISH_ONLY.translate()).isEqualTo("English");
  }

  @Test
  void testEnglishOnlyMessageWithChineseAndEnglishLocal() {
    CurrentLocaleHolder.setLocale(List.of(Locale.CHINESE, Locale.ENGLISH));
    assertThat(TEST_IN_ENGLISH_ONLY.translate()).isEqualTo("English");
  }

  @Test
  void testEnglishOnlyMessageWithFrenchAndEnglishLocal() {
    CurrentLocaleHolder.setLocale(List.of(Locale.FRENCH, Locale.ENGLISH));
    assertThat(TEST_IN_ENGLISH_ONLY.translate()).isEqualTo("English");
  }

  @Test
  void testFrenchOnlyMessageWithNoDefaultLocale() {
    assertThat(TEST_IN_FRENCH_ONLY.translate()).isEqualTo("TEST_IN_FRENCH_ONLY");
  }

  @Test
  void testFrenchOnlyMessageWithChineseLocal() {
    CurrentLocaleHolder.setLocale(List.of(Locale.CHINESE));
    assertThat(TEST_IN_FRENCH_ONLY.translate()).isEqualTo("TEST_IN_FRENCH_ONLY");
  }

  @Test
  void testFrenchOnlyMessageWithFrenchLocal() {
    CurrentLocaleHolder.setLocale(List.of(Locale.FRENCH));
    assertThat(TEST_IN_FRENCH_ONLY.translate()).isEqualTo("Français");
  }

  @Test
  void testFrenchAndEnglishMessageWithNoDefaultLocale() {
    assertThat(TEST_IN_FRENCH_AND_ENGLISH.translate()).isEqualTo("French and english");
  }

  @Test
  void testFrenchAndEnglishMessageWithChineseLocal() {
    CurrentLocaleHolder.setLocale(List.of(Locale.CHINESE));
    assertThat(TEST_IN_FRENCH_AND_ENGLISH.translate()).isEqualTo("French and english");
  }

  @Test
  void testFrenchAndEnglishMessageWithFrenchLocal() {
    CurrentLocaleHolder.setLocale(List.of(Locale.FRENCH));
    assertThat(TEST_IN_FRENCH_AND_ENGLISH.translate()).isEqualTo("Français et anglais");
  }

  @Test
  void testWithNoTranslationWithNoDefaultLocale() {
    assertThat(TEST_WITH_NO_TRANSLATION.translate()).isEqualTo("TEST_WITH_NO_TRANSLATION");
  }

  @Test
  void testWithNoTranslationWithChineseLocal() {
    CurrentLocaleHolder.setLocale(List.of(Locale.CHINESE));
    assertThat(TEST_WITH_NO_TRANSLATION.translate()).isEqualTo("TEST_WITH_NO_TRANSLATION");
  }

  @Test
  void testWithNoTranslationWithFrenchLocal() {
    CurrentLocaleHolder.setLocale(List.of(Locale.FRENCH));
    assertThat(TEST_WITH_NO_TRANSLATION.translate()).isEqualTo("TEST_WITH_NO_TRANSLATION");
  }

  @Test
  void testWithNoTranslationFileWithNoDefaultLocale() {
    assertThat(NotTranslatedTestEnum.TEST.translate()).isEqualTo("TEST");
  }

  @Test
  void testWithNoTranslationFileWithChineseLocal() {
    CurrentLocaleHolder.setLocale(List.of(Locale.CHINESE));
    assertThat(NotTranslatedTestEnum.TEST.translate()).isEqualTo("TEST");
  }

  @Test
  void testWithNoTranslationFileWithFrenchLocal() {
    CurrentLocaleHolder.setLocale(List.of(Locale.FRENCH));
    assertThat(NotTranslatedTestEnum.TEST.translate()).isEqualTo("TEST");
  }
}