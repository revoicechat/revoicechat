package fr.revoicechat.opengraph.service;

import org.jsoup.nodes.Document;

import fr.revoicechat.opengraph.OpenGraphBook;

final class OpenGraphBookMapper extends OpenGraphMapper<OpenGraphBook> {
  @Override
  public OpenGraphBook map(final Document doc) {
    return new OpenGraphBook(
        getMeta(doc, "book:author"),
        getMeta(doc, "book:isbn"),
        getMeta(doc, "book:release_date"),
        getMetas(doc, "book:tag")
    );
  }
}
