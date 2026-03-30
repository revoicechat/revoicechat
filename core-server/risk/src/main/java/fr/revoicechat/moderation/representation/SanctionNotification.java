package fr.revoicechat.moderation.representation;

import java.time.LocalDateTime;
import java.util.UUID;

import fr.revoicechat.moderation.model.Sanction;
import fr.revoicechat.moderation.model.SanctionType;
import fr.revoicechat.notification.model.NotificationPayload;
import fr.revoicechat.notification.model.NotificationType;

@NotificationType(name = "SANCTION")
public record SanctionNotification(UUID server,
                                   SanctionType type,
                                   LocalDateTime expiresAt,
                                   boolean active) implements NotificationPayload {
  public SanctionNotification(Sanction sanction) {
    this(sanction.getServer(), sanction.getType(), sanction.getExpiresAt(), sanction.isActive());
  }
}
