package fr.revoicechat.core.service.message;

import java.util.HashMap;
import java.util.Map;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.representation.message.MessageMention;
import fr.revoicechat.core.service.message.mension.MentionExtractor;

@ApplicationScoped
public class MessageMentionsExtractor {

  private final Instance<MentionExtractor> extractors;

  public MessageMentionsExtractor(final Instance<MentionExtractor> extractors) {
    this.extractors = extractors;
  }

  /// return a map with :
  /// * key : the mention value (for instance : `<@userId:550e8400-e29b-41d4-a716-446655440000>`)
  /// * value : the data corresponding
  public Map<String, MessageMention> extract(Message message) {
    Map<String, MessageMention> mentions = new HashMap<>();
    extractors.stream()
              .map(extractor -> extractor.extract(message))
              .forEach(result -> result.forEach((key, value) -> {
                if (mentions.put(key, value) != null) {
                  throw new IllegalStateException("Duplicate mention key: " + key);
                }
              }));
    return mentions;
  }
}
