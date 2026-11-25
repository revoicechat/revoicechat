package fr.revoicechat.risk.service.risk;

import fr.revoicechat.risk.service.DefaultRiskType;
import fr.revoicechat.risk.type.RiskType;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class RiskTypeDeserializer extends ValueDeserializer<RiskType> {

  @Override
  public RiskType deserialize(final JsonParser jsonParser, final DeserializationContext ctxt) throws JacksonException {
    return new DefaultRiskType(jsonParser.getValueAsString());
  }
}
