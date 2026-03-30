package fr.revoicechat.core.repository.impl;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.model.Emote;
import fr.revoicechat.core.repository.EmoteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class EmoteRepositoryImpl implements EmoteRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Stream<Emote> findByEntity(final UUID entity) {
    return entityManager.createQuery("select e from Emote e where e.entity = :entity", Emote.class)
                        .setParameter("entity", entity)
                        .getResultStream();
  }

  @Override
  public Stream<Emote> findGlobal() {
    return entityManager.createQuery("select e from Emote e where e.entity is null", Emote.class)
                        .getResultStream();
  }
}
