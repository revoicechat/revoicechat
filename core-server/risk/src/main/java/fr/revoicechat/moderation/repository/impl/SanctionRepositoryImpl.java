package fr.revoicechat.moderation.repository.impl;

import java.util.UUID;
import java.util.stream.Stream;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import fr.revoicechat.moderation.model.Sanction;
import fr.revoicechat.moderation.repository.SanctionRepository;

@ApplicationScoped
public class SanctionRepositoryImpl implements SanctionRepository {

  private static final String SERVER_ID = "serverId";
  private static final String USER_ID = "userId";

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
  public Stream<Sanction> findAll() {
    return entityManager.createQuery("select s from Sanction s", Sanction.class).getResultStream();
  }
}
