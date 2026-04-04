package fr.revoicechat.moderation.repository.impl;

import static java.lang.Boolean.TRUE;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.moderation.model.Sanction;
import fr.revoicechat.moderation.repository.SanctionRepository;
import fr.revoicechat.moderation.representation.SanctionFilterParams;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class SanctionRepositoryImpl implements SanctionRepository {

  private static final String APP = "app";

  private static final String SERVER_ID = "serverId";
  private static final String USER_ID = "userId";
  public static final String SERVER = "server";
  public static final String TARGETED_USER = "targetedUser";
  public static final String TYPE = "type";

  @PersistenceContext EntityManager entityManager;

  @Override
  public Stream<Sanction> getSanctions(final UUID userId) {
    return entityManager.createQuery("""
                            select s
                            from Sanction s
                            where s.targetedUser = :userId
                              and s.server is null
                            """, Sanction.class)
                        .setParameter(USER_ID, userId)
                        .getResultStream();
  }

  @Override
  public Stream<Sanction> getSanctions(final UUID userId, final UUID serverId) {
    return entityManager.createQuery("""
                            select s
                            from Sanction s
                            where s.targetedUser = :userId
                              and s.server = :serverId
                            """, Sanction.class)
                        .setParameter(USER_ID, userId)
                        .setParameter(SERVER_ID, serverId)
                        .getResultStream();
  }

  @Override
  public Stream<Sanction> findAll(final SanctionFilterParams params) {
    var cb = entityManager.getCriteriaBuilder();
    var query = cb.createQuery(Sanction.class);
    var root = query.from(Sanction.class);
    query.where(buildPredicates(params, cb, root));
    return entityManager.createQuery(query).getResultStream();
  }

  private List<Predicate> buildPredicates(SanctionFilterParams params, CriteriaBuilder cb, Root<Sanction> root) {
    List<Predicate> predicates = new ArrayList<>();
    if (params.getServerId() != null) {
      predicates.add(serverClause(params, cb, root));
    }
    if (params.getUserId() != null) {
      predicates.add(userClause(params, cb, root));
    }
    if (params.getType() != null) {
      predicates.add(typeClause(params, cb, root));
    }
    if (params.getActive() != null) {
      predicates.add(activeClause(params, cb, root));
    }
    return predicates;
  }

  private Predicate serverClause(SanctionFilterParams params, CriteriaBuilder cb, Root<Sanction> root) {
    return params.getServerId().equalsIgnoreCase(APP)
           ? cb.isNull(root.get(SERVER))
           : cb.equal(root.get(SERVER), parseUuid(params.getServerId()));
  }

  private Predicate userClause(SanctionFilterParams params, CriteriaBuilder cb, Root<Sanction> root) {
    return cb.equal(root.get(TARGETED_USER), params.getUserId());
  }

  private Predicate typeClause(SanctionFilterParams params, CriteriaBuilder cb, Root<Sanction> root) {
    return cb.equal(root.get(TYPE), params.getType());
  }

  private Predicate activeClause(SanctionFilterParams params, CriteriaBuilder cb, Root<Sanction> root) {
    var now = Instant.now();
    var notRevoked = cb.or(
        cb.isNull(root.get("revokedBy")),
        cb.isNull(root.get("revokedAt")),
        cb.greaterThanOrEqualTo(root.get("revokedAt"), now)
    );
    var notExpired = cb.or(
        cb.isNull(root.get("expiresAt")),
        cb.greaterThanOrEqualTo(root.get("expiresAt"), now)
    );
    var isActive = cb.and(notRevoked, notExpired);
    return TRUE.equals(params.getActive()) ? isActive : cb.not(isActive);
  }

  private UUID parseUuid(String value) {
    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException _) {
      throw new WebApplicationException("Invalid server ID: " + value, Response.Status.BAD_REQUEST);
    }
  }
}
