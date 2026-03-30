package fr.revoicechat.moderation.service;

import static fr.revoicechat.moderation.nls.SanctionErrorCode.*;
import static java.util.Comparator.comparing;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import fr.revoicechat.moderation.model.RequestStatus;
import fr.revoicechat.moderation.model.SanctionRevocationRequest;
import fr.revoicechat.moderation.model.Sanction;
import fr.revoicechat.moderation.repository.SanctionRevocationRequestRepository;
import fr.revoicechat.moderation.representation.SanctionNotification;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.notification.model.NotificationRegistrable.OnlineNotificationRegistrable;
import fr.revoicechat.security.UserHolder;
import fr.revoicechat.web.error.ResourceNotFoundException;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SanctionRevoker {

  private final UserHolder userHolder;
  private final EntityManager entityManager;
  private final SanctionEntityService sanctionEntityService;
  private final SanctionRevocationRequestRepository repository;

  public SanctionRevoker(UserHolder userHolder,
                         EntityManager entityManager,
                         SanctionEntityService sanctionEntityService,
                         SanctionRevocationRequestRepository repository) {
    this.userHolder = userHolder;
    this.entityManager = entityManager;
    this.sanctionEntityService = sanctionEntityService;
    this.repository = repository;
  }

  @Transactional
  public SanctionRevocationRequest ask(UUID sanctionId, String message) {
    var sanction = sanctionEntityService.get(sanctionId);
    if (!Objects.equals(sanction.getTargetedUser(), userHolder.getId())) {
      throw new UnauthorizedException(REVOKE_REQUEST_ONLY_ON_YOUR_SANCTION_ERROR.translate());
    }
    var lastRequest = repository.getBySanction(sanction)
                                .min(comparing(SanctionRevocationRequest::getRequestAt))
                                .orElse(null);
    if (lastRequest != null && !lastRequest.canRequestAgain()) {
      throw new UnauthorizedException(CANNOT_REQUEST_REVOKE_AGAIN_ERROR.translate());
    }
    var newRequest = new SanctionRevocationRequest();
    newRequest.setId(UUID.randomUUID());
    newRequest.setSanction(sanction);
    newRequest.setMessage(message);
    newRequest.setRequestAt(LocalDateTime.MIN);
    newRequest.setStatus(RequestStatus.CREATED);
    entityManager.persist(newRequest);
    return newRequest;
  }

  @Transactional
  public void revoke(final UUID serverId, final UUID id) {
    var sanction = sanctionEntityService.get(id);
    if (!Objects.equals(sanction.getServer(), serverId)) {
      throw new ResourceNotFoundException(Sanction.class, id);
    }
    sanction.setRevokedBy(userHolder.getId());
    sanction.setRevokedAt(LocalDateTime.now());
    entityManager.persist(sanction);
    updateStatus(sanction, RequestStatus.ACCEPTED);
    Notification.of(new SanctionNotification(sanction)).sendTo(new OnlineNotificationRegistrable(sanction.getTargetedUser()));
  }

  @Transactional
  public void rejectRequest(final UUID serverId, final UUID id) {
    var sanction = sanctionEntityService.get(id);
    if (!Objects.equals(sanction.getServer(), serverId)) {
      throw new ResourceNotFoundException(Sanction.class, id);
    }
    updateStatus(sanction, RequestStatus.REJECTED);
  }

  private void updateStatus(Sanction sanction, RequestStatus status) {
    repository.getBySanction(sanction)
              .min(comparing(SanctionRevocationRequest::getRequestAt))
              .ifPresent(lastRequest -> {
                lastRequest.setStatus(status);
                entityManager.persist(lastRequest);
              });
  }
}
