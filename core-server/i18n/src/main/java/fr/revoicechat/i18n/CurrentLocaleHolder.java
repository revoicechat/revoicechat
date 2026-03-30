package fr.revoicechat.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CurrentLocaleHolder {

  private CurrentLocaleHolder() {/*not instantiable*/}

  private static final ThreadLocal<List<Locale>> CURRENT_ACCEPTABLE_LANGUAGES = new ThreadLocal<>();

  static void setLocale(List<Locale> locale) {
    CURRENT_ACCEPTABLE_LANGUAGES.set(locale);
  }

  static List<Locale> getLocale() {
    var locales = Optional.ofNullable(CURRENT_ACCEPTABLE_LANGUAGES.get())
                          .map(ArrayList::new)
                          .orElseGet(ArrayList::new);
    locales.add(Locale.ENGLISH);
    return Collections.unmodifiableList(locales);
  }

  static void removeLocale() {
    CURRENT_ACCEPTABLE_LANGUAGES.remove();
  }
}
