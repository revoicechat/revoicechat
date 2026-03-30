package fr.revoicechat.moderation.repository.impl;

import java.util.UUID;
import java.util.stream.Stream;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import fr.revoicechat.moderation.model.RequestStatus;
import fr.revoicechat.moderation.model.Sanction;
import fr.revoicechat.moderation.model.SanctionRevocationRequest;
import fr.revoicechat.moderation.repository.SanctionRevocationRequestRepository;

@ApplicationScoped
public class SanctionRevocationRequestRepositoryImpl implements SanctionRevocationRequestRepository {

  private static final String SANCTION = "sanction";
  private static final String USER_ID = "userId";
  private static final String SERVER_ID = "serverId";

  @PersistenceContext EntityManager entityManager;

  @Override
  public Stream<SanctionRevocationRequest> getBySanction(final Sanction sanction) {
    return entityManager.createQuery("""
                                         select rsr
                                         from SanctionRevocationRequest rsr
                                         where rsr.sanction = :sanction
                                         """, SanctionRevocationRequest.class)
                        .setParameter(SANCTION, sanction)
                        .getResultStream();
  }

  @Override
  public Stream<SanctionRevocationRequest> getByUser(final UUID userId) {
    return entityManager.createQuery("""
                                         select rsr
                                         from SanctionRevocationRequest rsr
                                         where rsr.sanction.targetedUser = :userId
                                         """, SanctionRevocationRequest.class)
                        .setParameter(USER_ID, userId)
                        .getResultStream();
  }

  @Override
  public Stream<SanctionRevocationRequest> getByServer(final UUID serverId) {
    return entityManager.createQuery("""
                                         select rsr
                                         from SanctionRevocationRequest rsr
                                         where rsr.sanction.server = :serverId
                                           and rsr.status = :status
                                         """, SanctionRevocationRequest.class)
                        .setParameter(SERVER_ID, serverId)
                        .setParameter("status", RequestStatus.CREATED)
                        .getResultStream();
  }
}
