package fr.revoicechat.core.notification;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import fr.revoicechat.core.representation.RoomRepresentation;
import fr.revoicechat.notification.data.NotificationActionType;
import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationType;

@NotificationType(name = "ROOM_UPDATE")
@Schema(description = "Room notification")
public record RoomNotification(RoomRepresentation room, NotificationActionType action) implements NotificationPayload {}