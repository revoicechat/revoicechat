package fr.revoicechat.risk.service.server;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.risk.model.ServerRoles;
import fr.revoicechat.risk.representation.CreatedServerRoleRepresentation;
import fr.revoicechat.risk.service.risk.RiskCreator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ServerRoleCreator implements ServerRoleDefaultCreator{

  private final EntityManager entityManager;
  private final ServerFinder serverFinder;
  private final RiskCreator riskCreator;

  public ServerRoleCreator(EntityManager entityManager, ServerFinder serverFinder, RiskCreator riskCreator) {
    this.entityManager = entityManager;
    this.serverFinder = serverFinder;
    this.riskCreator = riskCreator;
  }

  @Transactional
  @Override
  public void createDefault(final UUID serverId) {
    var roles = createRole(serverId, new CreatedServerRoleRepresentation("default", "#ff5eff", 10000, List.of()));
    roles.setDefaultRole(true);
    entityManager.persist(roles);
  }

  @Transactional
  public ServerRoles createRole(final UUID serverId, final CreatedServerRoleRepresentation representation) {
    serverFinder.existsOrThrow(serverId);
    ServerRoles roles = new ServerRoles();
    roles.setId(UUID.randomUUID());
    roles.setServer(serverId);
    roles.setPriority(representation.priority());
    roles.setColor(representation.color());
    roles.setName(representation.name());
    entityManager.persist(roles);
    mapRisks(representation, roles);
    return roles;
  }

  private void mapRisks(final CreatedServerRoleRepresentation representation, final ServerRoles roles) {
    representation.risks().forEach(risk -> riskCreator.create(roles, risk));
  }
}
