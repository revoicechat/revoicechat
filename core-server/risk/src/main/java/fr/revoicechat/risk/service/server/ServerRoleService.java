package fr.revoicechat.risk.service.server;

import static fr.revoicechat.risk.type.RoleRiskType.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import fr.revoicechat.notification.Notification;
import fr.revoicechat.notification.data.NotificationActionType;
import fr.revoicechat.risk.model.Risk;
import fr.revoicechat.risk.model.RiskMode;
import fr.revoicechat.risk.model.ServerRoles;
import fr.revoicechat.risk.model.UserRoleMembership;
import fr.revoicechat.risk.repository.RiskRepository;
import fr.revoicechat.risk.repository.ServerRolesRepository;
import fr.revoicechat.risk.representation.CreatedServerRoleRepresentation;
import fr.revoicechat.risk.representation.NotificationServerRole;
import fr.revoicechat.risk.representation.RiskRepresentation;
import fr.revoicechat.risk.representation.ServerRoleRepresentation;
import fr.revoicechat.risk.service.risk.RiskCreator;
import fr.revoicechat.risk.service.user.UserRiskService;
import fr.revoicechat.risk.service.user.UserServerFinder;
import fr.revoicechat.risk.type.RiskType;
import fr.revoicechat.web.error.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ServerRoleService {

  private final UserRiskService userRiskService;
  private final RiskRepository riskRepository;
  private final ServerRolesRepository serverRolesRepository;
  private final EntityManager entityManager;
  private final ServerRoleCreator serverRoleCreator;
  private final UserServerFinder userServerFinder;
  private final RiskCreator riskCreator;

  public ServerRoleService(UserRiskService userRiskService,
                           RiskRepository riskRepository,
                           ServerRolesRepository serverRolesRepository,
                           EntityManager entityManager,
                           ServerRoleCreator serverRoleCreator,
                           UserServerFinder userServerFinder,
                           RiskCreator riskCreator) {
    this.userRiskService = userRiskService;
    this.riskRepository = riskRepository;
    this.serverRolesRepository = serverRolesRepository;
    this.entityManager = entityManager;
    this.serverRoleCreator = serverRoleCreator;
    this.userServerFinder = userServerFinder;
    this.riskCreator = riskCreator;
  }

  @Transactional
  public List<ServerRoleRepresentation> getByServer(UUID serverId) {
    return serverRolesRepository.getByServer(serverId).map(this::mapToRepresentation).toList();
  }

  public ServerRoleRepresentation get(final UUID roleId) {
    return mapToRepresentation(getEntity(roleId));
  }

  @Transactional
  public ServerRoleRepresentation create(final UUID serverId, final CreatedServerRoleRepresentation representation) {
    userRiskService.controlRiskPriority(representation.priority(), serverId, ADD_ROLE);
    ServerRoles roles = serverRoleCreator.createRole(serverId, representation);
    var roleRepresentation = mapToRepresentation(roles);
    Notification.of(new NotificationServerRole(roleRepresentation, NotificationActionType.ADD)).sendTo(userServerFinder.findUserForServer(serverId));
    return roleRepresentation;
  }

  @Transactional
  public ServerRoleRepresentation update(final UUID serverRoleId, final CreatedServerRoleRepresentation representation) {
    ServerRoles roles = getEntity(serverRoleId);
    userRiskService.controlRiskPriority(roles.getPriority(), roles.getServer(), UPDATE_ROLE);
    roles.setPriority(representation.priority());
    roles.setColor(representation.color());
    roles.setName(representation.name());
    entityManager.persist(roles);
    riskRepository.getRisks(serverRoleId).forEach(entityManager::remove);
    mapRisks(representation, roles);
    var roleRepresentation = mapToRepresentation(roles);
    Notification.of(new NotificationServerRole(roleRepresentation, NotificationActionType.MODIFY)).sendTo(userServerFinder.findUserForServer(roles.getServer()));
    return roleRepresentation;
  }

  private void mapRisks(final CreatedServerRoleRepresentation representation, final ServerRoles roles) {
    representation.risks().forEach(risk -> riskCreator.create(roles, risk));
  }

  public ServerRoleRepresentation mapToRepresentation(ServerRoles roles) {
    return new ServerRoleRepresentation(
        roles.getId(),
        roles.getName(),
        roles.getColor(),
        roles.getPriority(),
        roles.getServer(),
        riskRepository.getRisks(roles.getId())
                      .map(risk -> new RiskRepresentation(risk.getType(), risk.getEntity(), risk.getMode()))
                      .toList(),
        serverRolesRepository.getMembers(roles.getId())
    );
  }

  @Transactional
  public void addUserToDefaultServerRole(UUID user, UUID server) {
    serverRolesRepository.getDefaultServerRoles(server)
                         .forEach(serverRoles -> addRoleToUser(serverRoles, List.of(user)));
  }

  @Transactional
  public void addRoleToUser(final UUID serverRoleId, final List<UUID> users) {
    ServerRoles roles = getEntity(serverRoleId);
    userRiskService.controlRiskPriority(roles.getPriority(), roles.getServer(), ADD_USER_ROLE);
    addRoleToUser(roles, users);
  }

  @Transactional
  public void addRoleToUser(ServerRoles roles, final List<UUID> users) {
    users.stream()
         .map(user -> entityManager.find(UserRoleMembership.class, user))
         .filter(Objects::nonNull)
         .forEach(user -> {
           user.getServerRoles().add(roles);
           entityManager.persist(user);
         });
    Notification.of(new NotificationServerRole(mapToRepresentation(roles), NotificationActionType.MODIFY)).sendTo(userServerFinder.findUserForServer(roles.getServer()));
  }

  @Transactional
  public void removeUserToRole(final UUID serverRoleId, final List<UUID> users) {
    ServerRoles roles = getEntity(serverRoleId);
    userRiskService.controlRiskPriority(roles.getPriority(), roles.getServer(), ADD_USER_ROLE);
    users.stream()
         .map(user -> entityManager.find(UserRoleMembership.class, user))
         .filter(Objects::nonNull)
         .forEach(user -> {
           user.getServerRoles().remove(roles);
           entityManager.persist(user);
         });
    var roleRepresentation = mapToRepresentation(roles);
    Notification.of(new NotificationServerRole(roleRepresentation, NotificationActionType.MODIFY)).sendTo(userServerFinder.findUserForServer(roles.getServer()));
  }

  @Transactional
  public void addRiskOrReplace(final UUID roleId, UUID entity, final RiskType type, final RiskMode mode) {
    ServerRoles roles = getEntity(roleId);
    userRiskService.controlRiskPriority(roles.getPriority(), roles.getServer(), UPDATE_ROLE);
    riskRepository.getRisks(roleId)
                  .filter(risk -> risk.getType().equals(type))
                  .filter(risk -> Objects.equals(risk.getEntity(), entity))
                  .findFirst()
                  .ifPresentOrElse(risk -> updateMode(risk, mode),
                      () -> riskCreator.create(roles, new RiskRepresentation(type, entity, mode))
                  );
    var roleRepresentation = mapToRepresentation(roles);
    Notification.of(new NotificationServerRole(roleRepresentation, NotificationActionType.MODIFY)).sendTo(userServerFinder.findUserForServer(roles.getServer()));
  }

  private ServerRoles getEntity(final UUID serverRoleId) {
    ServerRoles roles = entityManager.find(ServerRoles.class, serverRoleId);
    if (roles == null) {
      throw new ResourceNotFoundException(ServerRoles.class, serverRoleId);
    }
    return roles;
  }

  private void updateMode(Risk risk, RiskMode mode) {
    risk.setMode(mode);
    entityManager.persist(risk);
  }
}
