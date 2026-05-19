package fr.revoicechat.core.service.message.textextractor;

import java.util.Collection;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.representation.message.TextPattern;

@ApplicationScoped
public class MessageTextPatternExtractor {

  private final Instance<TextPatternExtractor> extractors;

  public MessageTextPatternExtractor(final Instance<TextPatternExtractor> extractors) {
    this.extractors = extractors;
  }

  /// return a map with :
  /// * key : the mention value (for instance : `<@userId:550e8400-e29b-41d4-a716-446655440000>`)
  /// * value : the data corresponding
  public List<TextPattern> extract(Message message) {
    return extractors.stream()
                     .map(extractor -> extractor.extract(message))
                     .flatMap(Collection::stream)
                     .toList();
  }
}
