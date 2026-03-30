package fr.revoicechat.notification.stub;

import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationType;

@NotificationType(name = "MOCK")
public record NotificationPayloadMock(String name) implements NotificationPayload {}