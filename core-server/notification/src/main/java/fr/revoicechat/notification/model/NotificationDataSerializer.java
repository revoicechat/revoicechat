package fr.revoicechat.notification.model;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class NotificationDataSerializer extends JsonSerializer<NotificationData> {

  @Override
  public void serialize(NotificationData value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    gen.writeStartObject();
    NotificationPayload payload = value.data();
    NotificationType annotation = Objects.requireNonNull(payload.getClass().getDeclaredAnnotation(NotificationType.class));
    gen.writeStringField("type", annotation.name());
    gen.writeObjectField("data", payload);
    gen.writeEndObject();
  }
}
