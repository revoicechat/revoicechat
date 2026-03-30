package fr.revoicechat.opengraph.service;

import java.util.List;
import java.util.Optional;

import org.jsoup.nodes.Document;

abstract class OpenGraphMapper<T> {

  public abstract T map(Document doc);

  protected List<String> getMetas(final Document document, final String property) {
    return Optional.ofNullable(document)
                   .stream()
                   .flatMap(doc -> doc.selectStream("meta[property=" + property + "]"))
                   .map(elem -> elem.attr("content"))
                   .filter(el -> !el.isBlank())
                   .toList();
  }

  protected String getMeta(Document document, String property) {
    return Optional.ofNullable(document)
                   .map(doc -> doc.selectFirst("meta[property=" + property + "]"))
                   .map(elem -> elem.attr("content"))
                   .filter(el -> !el.isBlank())
                   .orElse(null);
  }
}
