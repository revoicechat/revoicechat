package fr.revoicechat.core.repository.jpa;

import java.util.ArrayList;

import fr.revoicechat.core.model.MessageReactions;
import jakarta.persistence.AttributeConverter;
import tools.jackson.databind.ObjectMapper;

public class MessageReactionsConverter implements AttributeConverter<MessageReactions, String> {

  private final ObjectMapper mapper;

  public MessageReactionsConverter() {
    this(new ObjectMapper());
  }

  MessageReactionsConverter(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public String convertToDatabaseColumn(final MessageReactions serverStructure) {
    return mapper.writeValueAsString(serverStructure);
  }

  @Override
  public MessageReactions convertToEntityAttribute(final String value) {
    if (value == null || value.isBlank()) {
      return new MessageReactions(new ArrayList<>());
    }
    return mapper.readValue(value, MessageReactions.class);
  }
}
