package fr.revoicechat.risk.service.server;

import java.util.UUID;

import fr.revoicechat.risk.repository.RiskRepository;
import fr.revoicechat.risk.repository.ServerRolesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class ServerRolesDeleterService {

  private final RiskRepository riskRepository;
  private final ServerRolesRepository serverRolesRepository;
  private final EntityManager entityManager;

  public ServerRolesDeleterService(final RiskRepository riskRepository, final ServerRolesRepository serverRolesRepository, final EntityManager entityManager) {
    this.riskRepository = riskRepository;
    this.serverRolesRepository = serverRolesRepository;
    this.entityManager = entityManager;
  }

  public void delete(UUID serverId) {
    serverRolesRepository.getByServer(serverId).forEach(roles -> {
      riskRepository.getRisks(roles.getId()).forEach(entityManager::remove);
      serverRolesRepository.deleteMembership(roles);
      entityManager.remove(roles);
    });
  }
}
