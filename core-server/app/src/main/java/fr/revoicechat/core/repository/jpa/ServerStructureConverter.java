package fr.revoicechat.core.repository.jpa;

import java.io.IOError;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.revoicechat.core.model.server.ServerStructure;
import jakarta.persistence.AttributeConverter;

public class ServerStructureConverter implements AttributeConverter<ServerStructure, String> {

  private final ObjectMapper mapper;

  public ServerStructureConverter() {
    this(new ObjectMapper());
  }

  ServerStructureConverter(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public String convertToDatabaseColumn(final ServerStructure serverStructure) {
    try {
      return mapper.writeValueAsString(serverStructure);
    } catch (JsonProcessingException e) {
      throw new IOError(e);
    }
  }

  @Override
  public ServerStructure convertToEntityAttribute(final String value) {
    if (value == null || value.isBlank()) {
      return new ServerStructure(List.of());
    }
    try {
      return mapper.readValue(value, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      throw new IOError(e);
    }
  }
}
