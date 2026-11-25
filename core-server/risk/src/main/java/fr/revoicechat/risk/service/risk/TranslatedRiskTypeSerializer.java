package fr.revoicechat.risk.service.risk;

import fr.revoicechat.risk.representation.RiskCategoryRepresentation.TranslatedRisk;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class TranslatedRiskTypeSerializer extends ValueSerializer<TranslatedRisk> {

  @Override
  public void serialize(final TranslatedRisk type, final JsonGenerator gen, final SerializationContext ctxt) throws JacksonException {
    gen.writeStartObject();
    gen.writeStringProperty("type", type.type().name());
    gen.writePOJOProperty("title", type.type().translate());
    gen.writeEndObject();
  }
}
