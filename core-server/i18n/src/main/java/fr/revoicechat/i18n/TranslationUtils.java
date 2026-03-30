package fr.revoicechat.i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class TranslationUtils {

  private TranslationUtils() {/*not instantiable*/}

  private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

  public static String translate(LocalizedMessage message, Object... args) {
    return translate(message.fileName(), message.name(), args);
  }

  public static String translate(String fileName, String name, Object... args) {
    return translate(fileName, name, CurrentLocaleHolder.getLocale(), args);
  }

  private static String translate(String fileName, String name, List<Locale> acceptedLanguage, Object... args) {
    for (Locale locale : acceptedLanguage) {
      try {
        ResourceBundle bundle = ResourceBundle.getBundle(fileName, locale, new FallbackToEnglishControl());
        String pattern = bundle.getString(name);
        return MessageFormat.format(pattern, args);
      } catch (MissingResourceException _) {
        // is the first accepted language is not present, we test the next one
      }
    }
    return name;
  }

  private static class FallbackToEnglishControl extends ResourceBundle.Control {
    @Override
    public Locale getFallbackLocale(String baseName, Locale locale) {
      return DEFAULT_LOCALE.equals(locale) ? null : DEFAULT_LOCALE;
    }
  }
}
