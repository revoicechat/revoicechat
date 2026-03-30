package fr.revoicechat.opengraph.service;

import org.jsoup.nodes.Document;

import fr.revoicechat.opengraph.OpenGraphImage;

final class OpenGraphImageMapper extends OpenGraphMapper<OpenGraphImage> {

  @Override
  public OpenGraphImage map(final Document doc) {
    return new OpenGraphImage(
        getMeta(doc, "og:image"),
        getMeta(doc, "og:image:url"),
        getMeta(doc, "og:image:secure_url"),
        getMeta(doc, "og:image:type"),
        getMeta(doc, "og:image:width"),
        getMeta(doc, "og:image:height"),
        getMeta(doc, "og:image:alt")
    );
  }
}
