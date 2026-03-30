package fr.revoicechat.live.stream.notification;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationType;

@NotificationType(name = "STREAM_STOP")
@Schema(description = "Stream stop")
public record StreamStop(UUID user, String name) implements NotificationPayload {}
