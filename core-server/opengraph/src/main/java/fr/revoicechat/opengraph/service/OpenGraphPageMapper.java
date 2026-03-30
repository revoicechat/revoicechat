package fr.revoicechat.opengraph.service;

import org.jsoup.nodes.Document;

import fr.revoicechat.opengraph.OpenGraphPage;

final class OpenGraphPageMapper extends OpenGraphMapper<OpenGraphPage> {
  @Override
  public OpenGraphPage map(final Document doc) {
    return new OpenGraphPage(
        getMeta(doc, "og:url"),
        getMeta(doc, "og:description"),
        getMeta(doc, "og:site_name"),
        getMeta(doc, "og:locale"),
        getMeta(doc, "og:locale:alternate")
    );
  }
}
