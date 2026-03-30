package fr.revoicechat.risk.repository.impl;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.risk.model.Risk;
import fr.revoicechat.risk.repository.RiskRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class RiskRepositoryImpl implements RiskRepository {

  @PersistenceContext EntityManager entityManager;

  @Override
  public Stream<Risk> getRisks(final UUID server) {
    return entityManager.createQuery("""
                            select r
                            from Risk r
                            where r.serverRoles.id = :server""", Risk.class)
                        .setParameter("server", server)
                        .getResultStream();
  }
}
