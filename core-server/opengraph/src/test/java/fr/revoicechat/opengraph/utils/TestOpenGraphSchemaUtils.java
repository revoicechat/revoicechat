package fr.revoicechat.opengraph.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import fr.revoicechat.opengraph.OpenGraphArticle;
import fr.revoicechat.opengraph.OpenGraphAudio;
import fr.revoicechat.opengraph.OpenGraphBasicData;
import fr.revoicechat.opengraph.OpenGraphBook;
import fr.revoicechat.opengraph.OpenGraphImage;
import fr.revoicechat.opengraph.OpenGraphMusic;
import fr.revoicechat.opengraph.OpenGraphPage;
import fr.revoicechat.opengraph.OpenGraphProfile;
import fr.revoicechat.opengraph.OpenGraphSchema;
import fr.revoicechat.opengraph.OpenGraphSchemaBuilder;
import fr.revoicechat.opengraph.OpenGraphVideo;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class TestOpenGraphSchemaUtils {

  private static final OpenGraphBasicData OPEN_GRAPH_BASIC_DATA = new OpenGraphBasicData("test", "test", "test");
  private static final OpenGraphImage OPEN_GRAPH_IMAGE = new OpenGraphImage("test", "test", "test", "test", "test", "test", "test");
  private static final OpenGraphBook OPEN_GRAPH_BOOK = new OpenGraphBook("test", "test", "test", List.of("test"));
  private static final OpenGraphPage OPEN_GRAPH_PAGE = new OpenGraphPage("test", "test", "test", "test", "test");
  private static final OpenGraphVideo OPEN_GRAPH_VIDEO = new OpenGraphVideo("test", "test", "test", "test", "test", "test");
  private static final OpenGraphAudio OPEN_GRAPH_AUDIO = new OpenGraphAudio("test", "test", "test");
  private static final OpenGraphArticle OPEN_GRAPH_ARTICLE = new OpenGraphArticle("test", "test", "test", "test", "test", List.of("test"));
  private static final OpenGraphProfile OPEN_GRAPH_PROFILE = new OpenGraphProfile("test", "test", "test", "test");
  private static final OpenGraphMusic OPEN_GRAPH_MUSIC = new OpenGraphMusic("test", "test", "test", "test", "test", "test", "test", "test", "test", "test");

  private static final OpenGraphSchema OPEN_GRAPH_SCHEMA = new OpenGraphSchemaBuilder().basic(OPEN_GRAPH_BASIC_DATA)
                                                                                      .image(OPEN_GRAPH_IMAGE)
                                                                                      .book(OPEN_GRAPH_BOOK)
                                                                                      .page(OPEN_GRAPH_PAGE)
                                                                                      .video(OPEN_GRAPH_VIDEO)
                                                                                      .audio(OPEN_GRAPH_AUDIO)
                                                                                      .article(OPEN_GRAPH_ARTICLE)
                                                                                      .profile(OPEN_GRAPH_PROFILE)
                                                                                      .music(OPEN_GRAPH_MUSIC)
                                                                                      .build();

  public static Stream<Arguments> openGraphSchema() {
    return Stream.of(
        Arguments.of(true, null),
        Arguments.of(true, new OpenGraphSchemaBuilder().build()),
        Arguments.of(false, new OpenGraphSchemaBuilder().basic(OPEN_GRAPH_BASIC_DATA).build()),
        Arguments.of(false, new OpenGraphSchemaBuilder().image(OPEN_GRAPH_IMAGE).build()),
        Arguments.of(false, new OpenGraphSchemaBuilder().book(OPEN_GRAPH_BOOK).build()),
        Arguments.of(false, new OpenGraphSchemaBuilder().page(OPEN_GRAPH_PAGE).build()),
        Arguments.of(false, new OpenGraphSchemaBuilder().video(OPEN_GRAPH_VIDEO).build()),
        Arguments.of(false, new OpenGraphSchemaBuilder().audio(OPEN_GRAPH_AUDIO).build()),
        Arguments.of(false, new OpenGraphSchemaBuilder().article(OPEN_GRAPH_ARTICLE).build()),
        Arguments.of(false, new OpenGraphSchemaBuilder().profile(OPEN_GRAPH_PROFILE).build()),
        Arguments.of(false, new OpenGraphSchemaBuilder().music(OPEN_GRAPH_MUSIC).build()),
        Arguments.of(false, OPEN_GRAPH_SCHEMA)
    );
  }

  @ParameterizedTest
  @MethodSource("openGraphSchema")
  void testOpenGraphSchema(boolean result, OpenGraphSchema data) {
    assertThat(OpenGraphSchemaUtils.isEmpty(data)).isEqualTo(result);
  }

  public static Stream<Arguments> openGraphBasicData() {
    return Stream.of(
        Arguments.of(true, null),
        Arguments.of(true, new OpenGraphBasicData("", "", "")),
        Arguments.of(true, new OpenGraphBasicData(null, null, null)),
        Arguments.of(false, new OpenGraphBasicData("test", null, null)),
        Arguments.of(false, new OpenGraphBasicData(null, "test", null)),
        Arguments.of(false, new OpenGraphBasicData(null, null, "test")),
        Arguments.of(false, OPEN_GRAPH_BASIC_DATA)
    );
  }

  @ParameterizedTest
  @MethodSource("openGraphBasicData")
  void testOpenGraphBasicData(boolean result, OpenGraphBasicData data) {
    assertThat(OpenGraphSchemaUtils.isEmpty(data)).isEqualTo(result);
  }

  public static Stream<Arguments> openGraphImage() {
    return Stream.of(
        Arguments.of(true, null),
        Arguments.of(true, new OpenGraphImage("", "", "", "", "", "", "")),
        Arguments.of(true, new OpenGraphImage(null, null, null, null, null, null, null)),
        Arguments.of(false, new OpenGraphImage("test", null, null, null, null, null, null)),
        Arguments.of(false, new OpenGraphImage(null, "test", null, null, null, null, null)),
        Arguments.of(false, new OpenGraphImage(null, null, "test", null, null, null, null)),
        Arguments.of(false, new OpenGraphImage(null, null, null, "test", null, null, null)),
        Arguments.of(false, new OpenGraphImage(null, null, null, null, "test", null, null)),
        Arguments.of(false, new OpenGraphImage(null, null, null, null, null, "test", null)),
        Arguments.of(false, new OpenGraphImage(null, null, null, null, null, null, "test")),
        Arguments.of(false, OPEN_GRAPH_IMAGE)
    );
  }

  @ParameterizedTest
  @MethodSource("openGraphImage")
  void testOpenGraphImage(boolean result, OpenGraphImage data) {
    assertThat(OpenGraphSchemaUtils.isEmpty(data)).isEqualTo(result);
  }

  public static Stream<Arguments> openGraphBook() {
    return Stream.of(
        Arguments.of(true, null),
        Arguments.of(true, new OpenGraphBook("", "", "", List.of())),
        Arguments.of(true, new OpenGraphBook(null, null, null, null)),
        Arguments.of(false, new OpenGraphBook("test", null, null, null)),
        Arguments.of(false, new OpenGraphBook(null, "test", null, null)),
        Arguments.of(false, new OpenGraphBook(null, null, "test", null)),
        Arguments.of(false, new OpenGraphBook(null, null, null, List.of("test"))),
        Arguments.of(false, OPEN_GRAPH_BOOK)
    );
  }

  @ParameterizedTest
  @MethodSource("openGraphBook")
  void testOpenGraphBook(boolean result, OpenGraphBook data) {
    assertThat(OpenGraphSchemaUtils.isEmpty(data)).isEqualTo(result);
  }

  public static Stream<Arguments> openGraphPage() {
    return Stream.of(
        Arguments.of(true, null),
        Arguments.of(true, new OpenGraphPage("", "", "", "", "")),
        Arguments.of(true, new OpenGraphPage(null, null, null, null, null)),
        Arguments.of(false, new OpenGraphPage("test", null, null, null, null)),
        Arguments.of(false, new OpenGraphPage(null, "test", null, null, null)),
        Arguments.of(false, new OpenGraphPage(null, null, "test", null, null)),
        Arguments.of(false, new OpenGraphPage(null, null, null, "test", null)),
        Arguments.of(false, new OpenGraphPage(null, null, null, null, "test")),
        Arguments.of(false, OPEN_GRAPH_PAGE)
    );
  }

  @ParameterizedTest
  @MethodSource("openGraphPage")
  void testOpenGraphPage(boolean result, OpenGraphPage data) {
    assertThat(OpenGraphSchemaUtils.isEmpty(data)).isEqualTo(result);
  }

  public static Stream<Arguments> openGraphVideo() {
    return Stream.of(
        Arguments.of(true, null),
        Arguments.of(true, new OpenGraphVideo("", "", "", "", "", "")),
        Arguments.of(true, new OpenGraphVideo(null, null, null, null, null, null)),
        Arguments.of(false, new OpenGraphVideo("test", null, null, null, null, null)),
        Arguments.of(false, new OpenGraphVideo(null, "test", null, null, null, null)),
        Arguments.of(false, new OpenGraphVideo(null, null, "test", null, null, null)),
        Arguments.of(false, new OpenGraphVideo(null, null, null, "test", null, null)),
        Arguments.of(false, new OpenGraphVideo(null, null, null, null, "test", null)),
        Arguments.of(false, new OpenGraphVideo(null, null, null, null, null, "test")),
        Arguments.of(false, OPEN_GRAPH_VIDEO)
    );
  }

  @ParameterizedTest
  @MethodSource("openGraphVideo")
  void testOpenGraphVideo(boolean result, OpenGraphVideo data) {
    assertThat(OpenGraphSchemaUtils.isEmpty(data)).isEqualTo(result);
  }

  public static Stream<Arguments> openGraphAudio() {
    return Stream.of(
        Arguments.of(true, null),
        Arguments.of(true, new OpenGraphAudio("", "", "")),
        Arguments.of(true, new OpenGraphAudio(null, null, null)),
        Arguments.of(false, new OpenGraphAudio("test", null, null)),
        Arguments.of(false, new OpenGraphAudio(null, "test", null)),
        Arguments.of(false, new OpenGraphAudio(null, null, "test")),
        Arguments.of(false, OPEN_GRAPH_AUDIO)
    );
  }

  @ParameterizedTest
  @MethodSource("openGraphAudio")
  void testOpenGraphAudio(boolean result, OpenGraphAudio data) {
    assertThat(OpenGraphSchemaUtils.isEmpty(data)).isEqualTo(result);
  }

  public static Stream<Arguments> openGraphArticle() {
    return Stream.of(
        Arguments.of(true, null),
        Arguments.of(true, new OpenGraphArticle("", "", "", "", "", List.of())),
        Arguments.of(true, new OpenGraphArticle(null, null, null, null, null, null)),
        Arguments.of(false, new OpenGraphArticle("test", null, null, null, null, null)),
        Arguments.of(false, new OpenGraphArticle(null, "test", null, null, null, null)),
        Arguments.of(false, new OpenGraphArticle(null, null, "test", null, null, null)),
        Arguments.of(false, new OpenGraphArticle(null, null, null, "test", null, null)),
        Arguments.of(false, new OpenGraphArticle(null, null, null, null, "test", null)),
        Arguments.of(false, new OpenGraphArticle(null, null, null, null, null, List.of("test"))),
        Arguments.of(false, OPEN_GRAPH_ARTICLE)
    );
  }

  @ParameterizedTest
  @MethodSource("openGraphArticle")
  void testOpenGraphArticle(boolean result, OpenGraphArticle data) {
    assertThat(OpenGraphSchemaUtils.isEmpty(data)).isEqualTo(result);
  }

  public static Stream<Arguments> openGraphProfile() {
    return Stream.of(
        Arguments.of(true, null),
        Arguments.of(true, new OpenGraphProfile("", "", "", "")),
        Arguments.of(true, new OpenGraphProfile(null, null, null, null)),
        Arguments.of(false, new OpenGraphProfile("test", null, null, null)),
        Arguments.of(false, new OpenGraphProfile(null, "test", null, null)),
        Arguments.of(false, new OpenGraphProfile(null, null, "test", null)),
        Arguments.of(false, new OpenGraphProfile(null, null, null, "test")),
        Arguments.of(false, OPEN_GRAPH_PROFILE)
    );
  }

  @ParameterizedTest
  @MethodSource("openGraphProfile")
  void testOpenGraphProfile(boolean result, OpenGraphProfile data) {
    assertThat(OpenGraphSchemaUtils.isEmpty(data)).isEqualTo(result);
  }

  public static Stream<Arguments> openGraphMusic() {
    return Stream.of(
        Arguments.of(true, null),
        Arguments.of(true, new OpenGraphMusic("", "", "", "", "", "", "", "", "", "")),
        Arguments.of(true, new OpenGraphMusic(null, null, null, null, null, null, null, null, null, null)),
        Arguments.of(false, new OpenGraphMusic("test", null, null, null, null, null, null, null, null, null)),
        Arguments.of(false, new OpenGraphMusic(null, "test", null, null, null, null, null, null, null, null)),
        Arguments.of(false, new OpenGraphMusic(null, null, "test", null, null, null, null, null, null, null)),
        Arguments.of(false, new OpenGraphMusic(null, null, null, "test", null, null, null, null, null, null)),
        Arguments.of(false, new OpenGraphMusic(null, null, null, null, "test", null, null, null, null, null)),
        Arguments.of(false, new OpenGraphMusic(null, null, null, null, null, "test", null, null, null, null)),
        Arguments.of(false, new OpenGraphMusic(null, null, null, null, null, null, "test", null, null, null)),
        Arguments.of(false, new OpenGraphMusic(null, null, null, null, null, null, null, "test", null, null)),
        Arguments.of(false, new OpenGraphMusic(null, null, null, null, null, null, null, null, "test", null)),
        Arguments.of(false, new OpenGraphMusic(null, null, null, null, null, null, null, null, null, "test")),
        Arguments.of(false, OPEN_GRAPH_MUSIC)
    );
  }

  @ParameterizedTest
  @MethodSource("openGraphMusic")
  void testOpenGraphMusic(boolean result, OpenGraphMusic data) {
    assertThat(OpenGraphSchemaUtils.isEmpty(data)).isEqualTo(result);
  }
}
