package fr.revoicechat.live.voice.notification;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationType;
import fr.revoicechat.notification.data.UserNotificationRepresentation;

@NotificationType(name = "VOICE_JOINING")
@Schema(description = "Voice joining notification")
public record VoiceJoiningNotification(UserNotificationRepresentation user, UUID roomId) implements NotificationPayload {}
