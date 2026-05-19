package fr.revoicechat.core.service.message.textextractor;

import java.util.List;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.representation.message.TextPattern;

/// Extractor of message text pattern
public interface TextPatternExtractor {
  List<TextPattern> extract(Message message);
}
