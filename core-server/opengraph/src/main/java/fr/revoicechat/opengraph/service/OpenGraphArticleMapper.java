package fr.revoicechat.opengraph.service;

import org.jsoup.nodes.Document;

import fr.revoicechat.opengraph.OpenGraphArticle;

final class OpenGraphArticleMapper extends OpenGraphMapper<OpenGraphArticle> {
  @Override
  public OpenGraphArticle map(final Document doc) {
    return new OpenGraphArticle(
        getMeta(doc, "article:published_time"),
        getMeta(doc, "article:modified_time"),
        getMeta(doc, "article:expiration_time"),
        getMeta(doc, "article:author"),
        getMeta(doc, "article:section"),
        getMetas(doc, "article:tag")
    );
  }
}
