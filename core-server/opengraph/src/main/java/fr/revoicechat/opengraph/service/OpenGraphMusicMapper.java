package fr.revoicechat.opengraph.service;

import org.jsoup.nodes.Document;

import fr.revoicechat.opengraph.OpenGraphMusic;

final class OpenGraphMusicMapper extends OpenGraphMapper<OpenGraphMusic> {
  @Override
  public OpenGraphMusic map(final Document doc) {
    return new OpenGraphMusic(
        getMeta(doc, "music:duration"),
        getMeta(doc, "music:album"),
        getMeta(doc, "music:album:disc"),
        getMeta(doc, "music:album:track"),
        getMeta(doc, "music:musician"),
        getMeta(doc, "music:song"),
        getMeta(doc, "music:song:disc"),
        getMeta(doc, "music:song:track"),
        getMeta(doc, "music:release_date"),
        getMeta(doc, "music:creator")
    );
  }
}
