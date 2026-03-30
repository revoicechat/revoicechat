package fr.revoicechat.opengraph.utils;

import java.util.List;

import fr.revoicechat.opengraph.OpenGraphArticle;
import fr.revoicechat.opengraph.OpenGraphAudio;
import fr.revoicechat.opengraph.OpenGraphBasicData;
import fr.revoicechat.opengraph.OpenGraphBook;
import fr.revoicechat.opengraph.OpenGraphImage;
import fr.revoicechat.opengraph.OpenGraphMusic;
import fr.revoicechat.opengraph.OpenGraphPage;
import fr.revoicechat.opengraph.OpenGraphProfile;
import fr.revoicechat.opengraph.OpenGraphSchema;
import fr.revoicechat.opengraph.OpenGraphVideo;

public final class OpenGraphSchemaUtils {
  private OpenGraphSchemaUtils() {}

  public static boolean isEmpty(OpenGraphSchema schema) {
    return schema == null || (
        isEmpty(schema.getBasic())
        && isEmpty(schema.getImage())
        && isEmpty(schema.getPage())
        && isEmpty(schema.getVideo())
        && isEmpty(schema.getAudio())
        && isEmpty(schema.getArticle())
        && isEmpty(schema.getBook())
        && isEmpty(schema.getProfile())
        && isEmpty(schema.getMusic())
    );
  }

  static boolean isEmpty(final OpenGraphBasicData basic) {
    return basic == null || (
        isEmpty(basic.title())
        && isEmpty(basic.url())
        && isEmpty(basic.type())
    );
  }

  static boolean isEmpty(final OpenGraphImage image) {
    return image == null || (
        isEmpty(image.image())
        && isEmpty(image.url())
        && isEmpty(image.secureUrl())
        && isEmpty(image.type())
        && isEmpty(image.width())
        && isEmpty(image.height())
        && isEmpty(image.alt())
    );
  }

  static boolean isEmpty(final OpenGraphBook book) {
    return book == null || (
        isEmpty(book.author())
        && isEmpty(book.isbn())
        && isEmpty(book.releaseDate())
        && isEmpty(book.tags())
    );
  }

  static boolean isEmpty(final OpenGraphPage page) {
    return page == null || (
        isEmpty(page.pageUrl())
        && isEmpty(page.description())
        && isEmpty(page.siteName())
        && isEmpty(page.locale())
        && isEmpty(page.localeAlternate())
    );
  }

  static boolean isEmpty(final OpenGraphVideo video) {
    return video == null || (
        isEmpty(video.video())
        && isEmpty(video.url())
        && isEmpty(video.secureUrl())
        && isEmpty(video.type())
        && isEmpty(video.width())
        && isEmpty(video.height())
    );
  }

  static boolean isEmpty(final OpenGraphAudio audio) {
    return audio == null || (
        isEmpty(audio.audio())
        && isEmpty(audio.secureUrl())
        && isEmpty(audio.type())
    );
  }

  static boolean isEmpty(final OpenGraphArticle article) {
    return article == null || (
        isEmpty(article.publishedTime())
        && isEmpty(article.modifiedTime())
        && isEmpty(article.expirationTime())
        && isEmpty(article.author())
        && isEmpty(article.section())
        && isEmpty(article.tags())
    );
  }

  static boolean isEmpty(final OpenGraphProfile profile) {
    return profile == null || (
        isEmpty(profile.firstName())
        && isEmpty(profile.lastName())
        && isEmpty(profile.username())
        && isEmpty(profile.gender())
    );
  }

  static boolean isEmpty(final OpenGraphMusic music) {
    return music == null || (
        isEmpty(music.duration())
        && isEmpty(music.album())
        && isEmpty(music.albumDisc())
        && isEmpty(music.albumTrack())
        && isEmpty(music.musician())
        && isEmpty(music.song())
        && isEmpty(music.songDisc())
        && isEmpty(music.songTrack())
        && isEmpty(music.releaseDate())
        && isEmpty(music.creator())
    );
  }

  private static boolean isEmpty(String value) {
    return value == null || value.isEmpty();
  }

  private static boolean isEmpty(List<String> values) {
    return values == null || values.isEmpty();
  }
}
