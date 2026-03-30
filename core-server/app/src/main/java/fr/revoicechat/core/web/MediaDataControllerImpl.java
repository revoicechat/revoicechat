package fr.revoicechat.core.web;

import static fr.revoicechat.core.model.MediaDataStatus.DELETING;
import static fr.revoicechat.core.technicaldata.media.UpdatableMediaDataStatus.STORED;
import static fr.revoicechat.notification.data.NotificationActionType.*;
import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.model.MediaDataStatus;
import fr.revoicechat.core.notification.service.MediaDataNotifierService;
import fr.revoicechat.core.representation.MediaDataRepresentation;
import fr.revoicechat.core.service.media.MediaDataService;
import fr.revoicechat.core.technicaldata.media.UpdatableMediaDataStatus;
import fr.revoicechat.core.web.api.MediaDataController;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

public class MediaDataControllerImpl implements MediaDataController {

  private final MediaDataService mediaDataService;
  private final MediaDataNotifierService notifierService;

  public MediaDataControllerImpl(final MediaDataService mediaDataService, final MediaDataNotifierService notifierService) {
    this.mediaDataService = mediaDataService;
    this.notifierService = notifierService;
  }

  @Override
  @PermitAll
  public MediaDataRepresentation get(final UUID id) {
    return Mapper.map(mediaDataService.getEntity(id));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public MediaDataRepresentation updateMediaByStatus(final UUID id, final UpdatableMediaDataStatus status) {
    var mediaData = mediaDataService.update(id, MediaDataStatus.valueOf(status.name()));
    notifierService.notify(mediaData, status.equals(STORED) ? MODIFY : REMOVE);
    return Mapper.map(mediaData);
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public List<MediaDataRepresentation> findMediaByStatus(final MediaDataStatus status) {
    return Mapper.mapAll(mediaDataService.findMediaByStatus(status));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public MediaDataRepresentation delete(final UUID id) {
    var mediaData = mediaDataService.update(id, DELETING);
    notifierService.notify(mediaData, REMOVE);
    return Mapper.map(mediaData);
  }
}
