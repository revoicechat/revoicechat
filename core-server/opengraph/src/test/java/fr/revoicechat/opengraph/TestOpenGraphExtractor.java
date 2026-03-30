package fr.revoicechat.opengraph;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;

@QuarkusTest
class TestOpenGraphExtractor {

  @Inject OpenGraphExtractor extractor;

  @Test
  void openGraphArticle() {
    String url = "http://localhost:%d/tests/open-graph/article".formatted(RestAssured.port);

    var result = extractor.extract(url);

    assertThat(result).isNotNull();
    assertThat(result.basic.title()).isEqualTo("The Future of Distributed Systems");
    assertThat(result.basic.type()).isEqualTo("article");
    assertThat(result.basic.url()).isEqualTo("https://example-tech.com/articles/distributed-systems");
    assertThat(result.page.description()).contains("An in-depth look at how distributed systems are evolving with edge computing, consensus algorithms, and the challenges of global-scale infrastructure.");
    assertThat(result.page.siteName()).isEqualTo("TechDepth");
    assertThat(result.page.locale()).isEqualTo("en_US");
    assertThat(result.image.image()).isEqualTo("https://picsum.photos/seed/distributed/1200/630");
    assertThat(result.image.url()).isEqualTo("https://picsum.photos/seed/distributed/1200/630");
    assertThat(result.image.secureUrl()).isEqualTo("https://picsum.photos/seed/distributed/1200/630");
    assertThat(result.image.type()).isEqualTo("image/jpeg");
    assertThat(result.image.width()).isEqualTo("1200");
    assertThat(result.image.height()).isEqualTo("630");
    assertThat(result.image.alt()).isEqualTo("Abstract visualization of interconnected nodes in a distributed network");
    assertThat(result.article.publishedTime()).isEqualTo("2024-03-15T09:00:00Z");
    assertThat(result.article.modifiedTime()).isEqualTo("2024-03-20T14:30:00Z");
    assertThat(result.article.expirationTime()).isEqualTo("2026-03-15T09:00:00Z");
    assertThat(result.article.author()).isEqualTo("https://example-tech.com/authors/jane-smith");
    assertThat(result.article.section()).isEqualTo("Engineering");
    assertThat(result.article.tags()).containsExactlyInAnyOrder("distributed systems", "cloud computing", "architecture");
  }

  @Test
  void openGraphBook() {
    String url = "http://localhost:%d/tests/open-graph/book".formatted(RestAssured.port);

    var result = extractor.extract(url);

    assertThat(result).isNotNull();
    assertThat(result.basic.title()).isEqualTo("A Map of Quiet Places");
    assertThat(result.basic.type()).isEqualTo("book");
    assertThat(result.basic.url()).isEqualTo("https://example-books.com/books/a-map-of-quiet-places");
    assertThat(result.page.description()).isEqualTo("A sweeping literary novel about memory, cartography, and the spaces between words. Winner of the 2023 Northern Pen Award.");
    assertThat(result.page.siteName()).isEqualTo("PageTurner");
    assertThat(result.page.locale()).isEqualTo("en_US");
    assertThat(result.page.localeAlternate()).isEqualTo("fr_FR");
    assertThat(result.image.image()).isEqualTo("https://picsum.photos/seed/bookcover/800/1200");
    assertThat(result.image.url()).isEqualTo("https://picsum.photos/seed/bookcover/800/1200");
    assertThat(result.image.secureUrl()).isEqualTo("https://picsum.photos/seed/bookcover/800/1200");
    assertThat(result.image.type()).isEqualTo("image/jpeg");
    assertThat(result.image.width()).isEqualTo("800");
    assertThat(result.image.height()).isEqualTo("1200");
    assertThat(result.image.alt()).isEqualTo("Book cover showing a faded topographic map with a single red dot");
    assertThat(result.book.author()).isEqualTo("https://example-books.com/authors/elena-voss");
    assertThat(result.book.isbn()).isEqualTo("978-3-16-148410-0");
    assertThat(result.book.releaseDate()).isEqualTo("2023-09-12");
    assertThat(result.book.tags()).containsExactlyInAnyOrder("literary fiction", "memory", "award-winning");
  }

  @Test
  void openGraphMusicAlbum() {
    String url = "http://localhost:%d/tests/open-graph/music-album".formatted(RestAssured.port);

    var result = extractor.extract(url);

    assertThat(result).isNotNull();
    assertThat(result.basic.title()).isEqualTo("Echoes in the Static");
    assertThat(result.basic.type()).isEqualTo("music.album");
    assertThat(result.basic.url()).isEqualTo("https://example-music.com/albums/echoes-in-the-static");
    assertThat(result.page.description()).isEqualTo("The third studio album by Neon Meridian, blending ambient electronica with post-rock sensibilities. Released June 2024.");
    assertThat(result.page.siteName()).isEqualTo("SoundVault");
    assertThat(result.page.locale()).isEqualTo("en_US");
    assertThat(result.image.image()).isEqualTo("https://picsum.photos/seed/album/1200/1200");
    assertThat(result.image.url()).isEqualTo("https://picsum.photos/seed/album/1200/1200");
    assertThat(result.image.secureUrl()).isEqualTo("https://picsum.photos/seed/album/1200/1200");
    assertThat(result.image.type()).isEqualTo("image/jpeg");
    assertThat(result.image.width()).isEqualTo("1200");
    assertThat(result.image.height()).isEqualTo("1200");
    assertThat(result.image.alt()).isEqualTo("Album cover for Echoes in the Static showing a glitchy cityscape at night");
    assertThat(result.audio.audio()).isEqualTo("https://example-music.com/previews/echoes-preview.mp3");
    assertThat(result.audio.secureUrl()).isEqualTo("https://example-music.com/previews/echoes-preview.mp3");
    assertThat(result.audio.type()).isEqualTo("audio/mpeg");
    assertThat(result.music.musician()).isEqualTo("https://example-music.com/artists/neon-meridian");
    assertThat(result.music.releaseDate()).isEqualTo("2024-06-01");
    assertThat(result.music.song()).isEqualTo("https://example-music.com/tracks/static-bloom");
    assertThat(result.music.songDisc()).isEqualTo("1");
    assertThat(result.music.songTrack()).isEqualTo("1");
    assertThat(result.music.creator()).isEqualTo("https://example-music.com/labels/meridian-records");
  }

  @Test
  void openGraphProfile() {
    String url = "http://localhost:%d/tests/open-graph/profile".formatted(RestAssured.port);

    var result = extractor.extract(url);

    assertThat(result).isNotNull();
    assertThat(result.basic.title()).isEqualTo("Marcus Holt – Software Engineer");
    assertThat(result.basic.type()).isEqualTo("profile");
    assertThat(result.basic.url()).isEqualTo("https://example-profiles.com/u/mholt");
    assertThat(result.page.description()).isEqualTo("Open source contributor, distributed systems enthusiast, and occasional blogger. Building things at the intersection of reliability and speed.");
    assertThat(result.page.siteName()).isEqualTo("DevProfiles");
    assertThat(result.page.locale()).isEqualTo("en_US");
    assertThat(result.image.image()).isEqualTo("https://picsum.photos/seed/profile-marcus/400/400");
    assertThat(result.image.url()).isEqualTo("https://picsum.photos/seed/profile-marcus/400/400");
    assertThat(result.image.secureUrl()).isEqualTo("https://picsum.photos/seed/profile-marcus/400/400");
    assertThat(result.image.type()).isEqualTo("image/jpeg");
    assertThat(result.image.width()).isEqualTo("400");
    assertThat(result.image.height()).isEqualTo("400");
    assertThat(result.image.alt()).isEqualTo("Profile photo of Marcus Holt");
    assertThat(result.profile.firstName()).isEqualTo("Marcus");
    assertThat(result.profile.lastName()).isEqualTo("Holt");
    assertThat(result.profile.username()).isEqualTo("mholt");
    assertThat(result.profile.gender()).isEqualTo("male");
  }

  @Test
  void openGraphVideo() {
    String url = "http://localhost:%d/tests/open-graph/video".formatted(RestAssured.port);

    var result = extractor.extract(url);

    assertThat(result).isNotNull();
    assertThat(result.basic.title()).isEqualTo("Building a Compiler from Scratch");
    assertThat(result.basic.type()).isEqualTo("video.other");
    assertThat(result.basic.url()).isEqualTo("https://example-video.com/watch/compiler-from-scratch");
    assertThat(result.page.description()).isEqualTo("A 3-hour walkthrough building a fully functional compiler for a toy language, covering lexing, parsing, AST construction, and code generation.");
    assertThat(result.page.siteName()).isEqualTo("CodeCast");
    assertThat(result.page.locale()).isEqualTo("en_US");
    assertThat(result.image.image()).isEqualTo("https://picsum.photos/seed/compiler/1280/720");
    assertThat(result.image.url()).isEqualTo("https://picsum.photos/seed/compiler/1280/720");
    assertThat(result.image.secureUrl()).isEqualTo("https://picsum.photos/seed/compiler/1280/720");
    assertThat(result.image.type()).isEqualTo("image/jpeg");
    assertThat(result.image.width()).isEqualTo("1280");
    assertThat(result.image.height()).isEqualTo("720");
    assertThat(result.image.alt()).isEqualTo("Thumbnail showing a code editor with compiler output side by side");
    assertThat(result.video.video()).isEqualTo("https://example-video.com/cdn/compiler-from-scratch.mp4");
    assertThat(result.video.url()).isEqualTo("https://example-video.com/cdn/compiler-from-scratch.mp4");
    assertThat(result.video.secureUrl()).isEqualTo("https://example-video.com/cdn/compiler-from-scratch.mp4");
    assertThat(result.video.type()).isEqualTo("video/mp4");
    assertThat(result.video.width()).isEqualTo("1280");
    assertThat(result.video.height()).isEqualTo("720");
  }

  @Test
  void openGraphEdgeCase() {
    String url = "http://localhost:%d/tests/open-graph/edge-cases".formatted(RestAssured.port);

    var result = extractor.extract(url);

    assertThat(result).isNotNull();
    assertThat(result.basic.title()).isEqualTo("A Page With Minimal OpenGraph");
    assertThat(result.basic.type()).isNull();
    assertThat(result.basic.url()).isEqualTo("https://example-edge.com/minimal");
    assertThat(result.page.description()).isNull();
    assertThat(result.page.siteName()).isNull();
    assertThat(result.page.locale()).isNull();
    assertThat(result.image.image()).isEqualTo("https://picsum.photos/seed/edge/600/300");
    assertThat(result.article.author()).isEqualTo("https://example-edge.com/authors/ghost");
  }

  @ParameterizedTest
  @ValueSource(strings = { "no-og", "json-data", "error-case" })
  void noOpenGraph(String path) {
    String url = "http://localhost:%d/tests/open-graph/%s".formatted(RestAssured.port, path);
    var result = extractor.extract(url);
    assertThat(result).isNull();
  }
}
