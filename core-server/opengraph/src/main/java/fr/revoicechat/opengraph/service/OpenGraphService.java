package fr.revoicechat.opengraph.service;

import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.revoicechat.opengraph.OpenGraphExtractor;
import fr.revoicechat.opengraph.OpenGraphSchema;
import fr.revoicechat.opengraph.OpenGraphSchemaBuilder;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
public class OpenGraphService implements OpenGraphExtractor {

  private static final Logger LOG = LoggerFactory.getLogger(OpenGraphService.class);

  private final HttpFetcher fetcher;

  public OpenGraphService(HttpFetcher fetcher) {
    this.fetcher = fetcher;
  }

  @Override
  public boolean hasPreview(final String text) {
    return text != null && UrlsExtractor.extract(text).size() == 1;
  }

  @Override
  @SuppressWarnings("java:S2142") // interruption error is not rethrow
  public OpenGraphSchema extract(String text) {
    if (text == null) {
      return null;
    }
    try {
      List<String> urls = UrlsExtractor.extract(text);
      if (urls.size() != 1) {
        return null;
      }
      String url = urls.getFirst();
      Document doc = fetcher.fetch(url);
      return new OpenGraphSchemaBuilder()
          .basic(new OpenGraphBasicDataMapper().map(doc))
          .image(new OpenGraphImageMapper().map(doc))
          .page(new OpenGraphPageMapper().map(doc))
          .video(new OpenGraphVideoMapper().map(doc))
          .audio(new OpenGraphAudioMapper().map(doc))
          .article(new OpenGraphArticleMapper().map(doc))
          .book(new OpenGraphBookMapper().map(doc))
          .profile(new OpenGraphProfileMapper().map(doc))
          .music(new OpenGraphMusicMapper().map(doc))
          .build();
    } catch (IOException | InterruptedException e) {
      LOG.debug("Error while extracting OpenGraph URLs", e);
      return null;
    }
  }

}