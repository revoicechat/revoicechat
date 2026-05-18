package fr.revoicechat.core.service.message.mension;

import java.util.Map;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.representation.message.MessageMention;

/// Extractor of mention
public interface MentionExtractor {
  Map<String, MessageMention> extract(Message message);
}
