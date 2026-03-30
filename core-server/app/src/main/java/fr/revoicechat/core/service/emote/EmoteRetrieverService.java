package fr.revoicechat.core.service.emote;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.model.Emote;
import fr.revoicechat.core.repository.EmoteRepository;
import fr.revoicechat.web.error.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EmoteRetrieverService {

  private final EntityManager entityManager;
  private final EmoteRepository emoteRepository;

  @Inject
  public EmoteRetrieverService(EntityManager entityManager, EmoteRepository emoteRepository) {
    this.entityManager = entityManager;
    this.emoteRepository = emoteRepository;
  }

  public Emote getEntity(final UUID id) {
    var emote = entityManager.find(Emote.class, id);
    if (emote == null) {
      throw new ResourceNotFoundException(Emote.class, id);
    }
    return emote;
  }

  @Transactional
  public List<Emote> getAll(final UUID id) {
    return emoteRepository.findByEntity(id).toList();
  }

  @Transactional
  public List<Emote> getGlobal() {
    return emoteRepository.findGlobal().toList();
  }
}
