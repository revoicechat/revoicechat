package fr.revoicechat.opengraph.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestUrlsExtractor {

  public static Stream<Arguments> sources() {
    return Stream.of(
        Arguments.of("https://google.com", List.of("https://google.com")),
        Arguments.of("https://google.com https://revoicechat.fr", List.of("https://google.com", "https://revoicechat.fr")),
        Arguments.of("this is a text with no url", List.of()),
        Arguments.of("""
                                Look in https://google.com
                                if you can find  https://revoicechat.fr""", List.of("https://google.com", "https://revoicechat.fr"))
    );
  }

  @ParameterizedTest
  @MethodSource("sources")
  void test(String input, List<String> output) {
    assertThat(UrlsExtractor.extract(input)).containsExactlyInAnyOrderElementsOf(output);
  }
}