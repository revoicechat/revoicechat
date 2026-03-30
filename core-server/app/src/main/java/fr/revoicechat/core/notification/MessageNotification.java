package fr.revoicechat.core.notification;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.notification.data.NotificationActionType;
import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationType;

@NotificationType(name = "ROOM_MESSAGE")
@Schema(description = "Message notification")
public record MessageNotification(MessageRepresentation message, NotificationActionType action) implements NotificationPayload {}