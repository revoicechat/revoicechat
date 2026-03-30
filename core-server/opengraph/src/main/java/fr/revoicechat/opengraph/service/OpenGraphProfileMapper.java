package fr.revoicechat.opengraph.service;

import org.jsoup.nodes.Document;

import fr.revoicechat.opengraph.OpenGraphProfile;

final class OpenGraphProfileMapper extends OpenGraphMapper<OpenGraphProfile> {
  @Override
  public OpenGraphProfile map(final Document doc) {
    return new OpenGraphProfile(
        getMeta(doc, "profile:first_name"),
        getMeta(doc, "profile:last_name"),
        getMeta(doc, "profile:username"),
        getMeta(doc, "profile:gender")
    );
  }
}
