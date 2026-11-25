package fr.revoicechat.notification.model;

import java.util.Objects;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

class NotificationDataSerializer extends ValueSerializer<NotificationData> {

  @Override
  public void serialize(final NotificationData value, final JsonGenerator gen, final SerializationContext ctxt) throws JacksonException {
    gen.writeStartObject();
    NotificationPayload payload = value.data();
    NotificationType annotation = Objects.requireNonNull(payload.getClass().getDeclaredAnnotation(NotificationType.class));
    gen.writeStringProperty("type", annotation.name());
    gen.writePOJOProperty("data", payload);
    gen.writeEndObject();
  }
}
