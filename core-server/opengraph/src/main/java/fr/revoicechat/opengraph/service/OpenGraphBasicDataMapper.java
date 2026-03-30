package fr.revoicechat.opengraph.service;

import org.jsoup.nodes.Document;

import fr.revoicechat.opengraph.OpenGraphBasicData;

final class OpenGraphBasicDataMapper extends OpenGraphMapper<OpenGraphBasicData> {

  @Override
  public OpenGraphBasicData map(final Document doc) {
    return new OpenGraphBasicData(
        getMeta(doc, "og:url"),
        getMeta(doc, "og:title"),
        getMeta(doc, "og:type")
    );
  }
}
