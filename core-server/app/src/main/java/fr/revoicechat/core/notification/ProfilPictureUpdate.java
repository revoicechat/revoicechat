package fr.revoicechat.core.notification;

import java.util.UUID;

import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationType;

@NotificationType(name = "PROFIL_PICTURE_UPDATE")
public record ProfilPictureUpdate(UUID id) implements NotificationPayload {}
