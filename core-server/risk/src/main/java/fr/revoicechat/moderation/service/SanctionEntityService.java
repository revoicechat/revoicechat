package fr.revoicechat.moderation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import fr.revoicechat.moderation.model.Sanction;
import fr.revoicechat.moderation.repository.SanctionRepository;
import fr.revoicechat.moderation.representation.SanctionFilterParams;
import fr.revoicechat.moderation.representation.SanctionRepresentation;
import fr.revoicechat.security.UserHolder;
import fr.revoicechat.web.error.ResourceNotFoundException;

@ApplicationScoped
public class SanctionEntityService {

  private final EntityManager entityManager;
  private final SanctionRepository sanctionRepository;

  public SanctionEntityService(EntityManager entityManager, SanctionRepository sanctionRepository) {
    this.entityManager = entityManager;
    this.sanctionRepository = sanctionRepository;
  }

  public Sanction get(final UUID id) {
    return Optional.of(entityManager.find(Sanction.class, id))
                   .orElseThrow(() -> new ResourceNotFoundException(Sanction.class, id));
  }

  @Transactional
  public List<Sanction> getAll(final UUID id, final SanctionFilterParams params) {
    var filterByParam = FilterByParam.of(params);
    return sanctionRepository
        .findAll()
        .filter(sanction -> Objects.equals(id, sanction.getServer()))
        .filter(filterByParam)
        .toList();
  }

  private record FilterByParam(List<Predicate<Sanction>> predicates) implements Predicate<Sanction> {
    static FilterByParam of(SanctionFilterParams params) {
      List<Predicate<Sanction>> predicates = new ArrayList<>();
      if (params.getUserId() != null) {
        predicates.add(sanction -> Objects.equals(sanction.getTargetedUser(), params.getUserId()));
      }
      if (params.getType() != null) {
        predicates.add(sanction -> Objects.equals(sanction.getType(), params.getType()));
      }
      if (params.getActive() != null) {
        predicates.add(sanction -> Objects.equals(sanction.isActive(), params.getActive()));
      }
      return new FilterByParam(predicates);
    }

    @Override
    public boolean test(final Sanction sanction) {
      return predicates.stream().allMatch(predicate -> predicate.test(sanction));
    }
  }
}
