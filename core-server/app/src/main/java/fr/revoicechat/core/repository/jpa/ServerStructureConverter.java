package fr.revoicechat.core.repository.jpa;

import java.util.List;

import fr.revoicechat.core.model.server.ServerStructure;
import jakarta.persistence.AttributeConverter;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

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
    return mapper.writeValueAsString(serverStructure);
  }

  @Override
  public ServerStructure convertToEntityAttribute(final String value) {
    if (value == null || value.isBlank()) {
      return new ServerStructure(List.of());
    }
    return mapper.readValue(value, new TypeReference<>() {});
  }
}
