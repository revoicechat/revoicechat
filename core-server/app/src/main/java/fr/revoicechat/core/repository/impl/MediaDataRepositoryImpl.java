package fr.revoicechat.core.repository.impl;

import java.util.stream.Stream;

import fr.revoicechat.core.model.MediaData;
import fr.revoicechat.core.model.MediaDataStatus;
import fr.revoicechat.core.repository.MediaDataRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class MediaDataRepositoryImpl implements MediaDataRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public Stream<MediaData> findByStatus(final MediaDataStatus status) {
    return em.createQuery("select m from MediaData m where m.status = :status", MediaData.class)
             .setParameter("status", status)
             .getResultStream();
  }
}
