package fr.revoicechat.moderation.service;

import java.time.LocalDateTime;
import java.util.UUID;

import fr.revoicechat.moderation.model.Sanction;
import fr.revoicechat.moderation.representation.NewSanction;
import fr.revoicechat.moderation.representation.SanctionNotification;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.notification.model.NotificationRegistrable.OnlineNotificationRegistrable;
import fr.revoicechat.security.UserHolder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SanctionCreator {

  private final UserHolder userHolder;
  private final EntityManager entityManager;

  public SanctionCreator(UserHolder userHolder, EntityManager entityManager) {
    this.userHolder = userHolder;
    this.entityManager = entityManager;
  }

  @Transactional
  public Sanction create(final UUID serverId, final NewSanction newSanction) {
    Sanction sanction = new Sanction();
    sanction.setId(UUID.randomUUID());
    sanction.setTargetedUser(newSanction.targetedUser());
    sanction.setServer(serverId);
    sanction.setType(newSanction.type());
    sanction.setStartAt(LocalDateTime.now());
    sanction.setExpiresAt(newSanction.expiresAt());
    sanction.setIssuedBy(userHolder.getId());
    sanction.setReason(newSanction.reason());
    entityManager.persist(sanction);
    Notification.of(new SanctionNotification(sanction)).sendTo(new OnlineNotificationRegistrable(sanction.getTargetedUser()));
    return sanction;
  }
}
