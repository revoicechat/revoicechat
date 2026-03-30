package fr.revoicechat.core.repository.jpa;

import java.io.IOError;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.revoicechat.core.model.MessageReactions;
import jakarta.persistence.AttributeConverter;

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
    try {
      return mapper.writeValueAsString(serverStructure);
    } catch (JsonProcessingException e) {
      throw new IOError(e);
    }
  }

  @Override
  public MessageReactions convertToEntityAttribute(final String value) {
    if (value == null || value.isBlank()) {
      return new MessageReactions(new ArrayList<>());
    }
    try {
      return mapper.readValue(value, MessageReactions.class);
    } catch (JsonProcessingException e) {
      throw new IOError(e);
    }
  }
}
