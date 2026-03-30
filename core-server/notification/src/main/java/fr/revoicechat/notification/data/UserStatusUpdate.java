package fr.revoicechat.notification.data;


import java.util.UUID;

import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationType;

@NotificationType(name = "USER_STATUS_UPDATE")
public record UserStatusUpdate(UUID userId, ActiveStatus status) implements NotificationPayload {}
