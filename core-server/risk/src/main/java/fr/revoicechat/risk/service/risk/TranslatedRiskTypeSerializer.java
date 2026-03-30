package fr.revoicechat.risk.service.risk;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.revoicechat.risk.representation.RiskCategoryRepresentation.TranslatedRisk;

public class TranslatedRiskTypeSerializer extends JsonSerializer<TranslatedRisk> {

  @Override
  public void serialize(final TranslatedRisk type, final JsonGenerator gen, final SerializerProvider serializerProvider) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("type", type.type().name());
    gen.writeObjectField("title", type.type().translate());
    gen.writeEndObject();

  }
}
