package fr.revoicechat.opengraph;

import fr.revoicechat.opengraph.utils.OpenGraphSchemaUtils;

public final class OpenGraphSchemaBuilder {
  private final OpenGraphSchema schema = new OpenGraphSchema();

  public OpenGraphSchema build() {
    return OpenGraphSchemaUtils.isEmpty(schema) ? null : schema;
  }

  public OpenGraphSchemaBuilder basic(OpenGraphBasicData basic) {
    schema.basic = basic;
    return this;
  }

  public OpenGraphSchemaBuilder image(OpenGraphImage image) {
    schema.image = image;
    return this;
  }

  public OpenGraphSchemaBuilder page(OpenGraphPage page) {
    schema.page = page;
    return this;
  }

  public OpenGraphSchemaBuilder video(OpenGraphVideo video) {
    schema.video = video;
    return this;
  }

  public OpenGraphSchemaBuilder audio(OpenGraphAudio audio) {
    schema.audio = audio;
    return this;
  }

  public OpenGraphSchemaBuilder article(OpenGraphArticle article) {
    schema.article = article;
    return this;
  }

  public OpenGraphSchemaBuilder book(OpenGraphBook book) {
    schema.book = book;
    return this;
  }

  public OpenGraphSchemaBuilder profile(OpenGraphProfile profile) {
    schema.profile = profile;
    return this;
  }

  public OpenGraphSchemaBuilder music(OpenGraphMusic music) {
    schema.music = music;
    return this;
  }
}