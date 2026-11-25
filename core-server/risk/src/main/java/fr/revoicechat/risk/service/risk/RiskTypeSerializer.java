package fr.revoicechat.risk.service.risk;

import fr.revoicechat.risk.type.RiskType;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class RiskTypeSerializer extends ValueSerializer<RiskType> {

  @Override
  public void serialize(final RiskType type, final JsonGenerator gen, final SerializationContext ctxt) throws JacksonException {
    gen.writeString(type.name());
  }
}
