package fr.revoicechat.notification.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = NotificationDataSerializer.class)
public record NotificationData(NotificationPayload data) {

  public static NotificationData ping() {
    return new NotificationData(new Ping());
  }

  @NotificationType(name = "PING")
  @Schema(description = "Ping")
  public static final class Ping implements NotificationPayload {
    public Ping() {super();}
  }
}
