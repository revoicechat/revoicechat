package fr.revoicechat.opengraph.service;

import org.jsoup.nodes.Document;

import fr.revoicechat.opengraph.OpenGraphVideo;

final class OpenGraphVideoMapper extends OpenGraphMapper<OpenGraphVideo> {
  @Override
  public OpenGraphVideo map(final Document doc) {
    return new OpenGraphVideo(
        getMeta(doc, "og:video"),
        getMeta(doc, "og:video:url"),
        getMeta(doc, "og:video:secure_url"),
        getMeta(doc, "og:video:type"),
        getMeta(doc, "og:video:width"),
        getMeta(doc, "og:video:height")
    );
  }
}
