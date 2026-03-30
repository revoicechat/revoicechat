package fr.revoicechat.risk.service.risk;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.revoicechat.risk.type.RiskType;

public class RiskTypeSerializer extends JsonSerializer<RiskType> {
  @Override
  public void serialize(final RiskType type, final JsonGenerator gen, final SerializerProvider serializerProvider) throws IOException {
    gen.writeString(type.name());
  }
}
