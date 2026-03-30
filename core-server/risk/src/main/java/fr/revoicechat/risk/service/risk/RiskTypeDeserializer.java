package fr.revoicechat.risk.service.risk;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.revoicechat.risk.service.DefaultRiskType;
import fr.revoicechat.risk.type.RiskType;

public class RiskTypeDeserializer extends JsonDeserializer<RiskType> {

  @Override
  public RiskType deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
    return new DefaultRiskType(jsonParser.getValueAsString());
  }
}
