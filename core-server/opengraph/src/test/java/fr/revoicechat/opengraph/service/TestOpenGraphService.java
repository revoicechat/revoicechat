package fr.revoicechat.opengraph.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestOpenGraphService {

  @Test
  void testWithOneUrl() {
    // Given
    var text = """
        look https://github.com/revoicechat for
        the ReVoiceChat repositories""";
    // When
    var result = new OpenGraphService(new HttpFetcherImpl()).extract(text);
    // Then
    assertThat(result).isNotNull();
    assertThat(result.getBasic().url()).isEqualTo("https://github.com/revoicechat");
    assertThat(result.getBasic().title()).isNotEmpty();
    assertThat(result.getPage().description()).isNotEmpty();
    assertThat(result.getPage().siteName()).isEqualTo("GitHub");
    assertThat(result.getImage().image()).isNotEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      """
          - https://github.com/revoicechat
          - https://github.com/revoicechat/revoicechat""",
      "no url",
      ""
  })
  void testNoGraph(String text) {
    assertThat(new OpenGraphService(new HttpFetcherImpl()).extract(text)
    ).isNull();
  }

  @Test
  void testWithNull() {
    assertThat(new OpenGraphService(new HttpFetcherImpl()).extract(null)).isNull();
  }

  @Test
  void testWithError() {
    assertThat(new OpenGraphService(_ -> {
      throw new IOException();
    }).extract("https://github.com/revoicechat")).isNull();
  }

  @Test
  void testHasPreview() {
    var service = new OpenGraphService(new HttpFetcherImpl());
    assertThat(service.hasPreview("""
        look https://github.com/revoicechat for
        the core part of ReVoiceChat""")).isTrue();
    assertThat(service.hasPreview("""
        - https://github.com/revoicechat
        - https://github.com/revoicechat/revoicechat""")).isFalse();
    assertThat(service.hasPreview("no url")).isFalse();
    assertThat(service.hasPreview("")).isFalse();
    assertThat(service.hasPreview(null)).isFalse();
  }
}