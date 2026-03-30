package fr.revoicechat.opengraph.service;

import org.jsoup.nodes.Document;

import fr.revoicechat.opengraph.OpenGraphAudio;

final class OpenGraphAudioMapper extends OpenGraphMapper<OpenGraphAudio> {
  @Override
  public OpenGraphAudio map(final Document doc) {
    return new OpenGraphAudio(
        getMeta(doc, "og:audio"),
        getMeta(doc, "og:audio:secure_url"),
        getMeta(doc, "og:audio:type")
    );
  }
}
