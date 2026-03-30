package fr.revoicechat.live.stream.notification;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationType;

@NotificationType(name = "STREAM_JOIN")
@Schema(description = "Stream join")
public record StreamJoin(UUID streamer,
                         String streamName,
                         UUID viewer) implements NotificationPayload {
}
