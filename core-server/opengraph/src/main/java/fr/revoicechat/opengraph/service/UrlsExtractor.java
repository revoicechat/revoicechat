package fr.revoicechat.opengraph.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class UrlsExtractor {

  private static final Pattern URL_PATTERN = Pattern.compile("(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)");

  private UrlsExtractor() {}

  static List<String> extract(String text) {
    List<String> urls = new ArrayList<>();
    Matcher matcher = URL_PATTERN.matcher(text);

    while (matcher.find()) {
      urls.add(matcher.group(1));
    }

    return urls;
  }
}
