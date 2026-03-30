package fr.revoicechat.core.service.media;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fr.revoicechat.core.model.MediaData;
import fr.revoicechat.core.model.MediaDataStatus;
import fr.revoicechat.core.model.MediaOrigin;
import fr.revoicechat.core.repository.MediaDataRepository;
import fr.revoicechat.core.technicaldata.media.NewMediaData;
import fr.revoicechat.web.error.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MediaDataService {

  private final EntityManager entityManager;
  private final MediaDataRepository mediaDataRepository;
  private final FileTypeDetermination fileTypeDetermination;

  public MediaDataService(final EntityManager entityManager,
                          final MediaDataRepository mediaDataRepository,
                          final FileTypeDetermination fileTypeDetermination) {
    this.entityManager = entityManager;
    this.mediaDataRepository = mediaDataRepository;
    this.fileTypeDetermination = fileTypeDetermination;
  }

  public MediaData getEntity(final UUID id) {
    return Optional.ofNullable(entityManager.find(MediaData.class, id))
                   .orElseThrow(() -> new ResourceNotFoundException(MediaData.class, id));
  }

  public MediaData create(final NewMediaData creation, MediaOrigin origin) {
    MediaData mediaData = new MediaData();
    mediaData.setId(UUID.randomUUID());
    mediaData.setName(creation.name());
    mediaData.setType(fileTypeDetermination.get(creation.name()));
    mediaData.setOrigin(origin);
    mediaData.setStatus(MediaDataStatus.DOWNLOADING);
    entityManager.persist(mediaData);
    return mediaData;
  }

  @Transactional
  public MediaData update(final UUID id, final MediaDataStatus status) {
    var mediaData = getEntity(id);
    mediaData.setStatus(MediaDataStatus.valueOf(status.name()));
    entityManager.persist(mediaData);
    return mediaData;
  }

  public List<MediaData> findMediaByStatus(final MediaDataStatus status) {
    return mediaDataRepository.findByStatus(status).toList();
  }
}
