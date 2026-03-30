package fr.revoicechat.risk.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.risk.RisksMembershipData;
import fr.revoicechat.risk.representation.CreatedServerRoleRepresentation;
import fr.revoicechat.risk.representation.RiskUpdateRepresentation;
import fr.revoicechat.risk.representation.ServerRoleRepresentation;
import fr.revoicechat.risk.retriever.ServerRoleIdRetriever;
import fr.revoicechat.risk.service.DefaultRiskType;
import fr.revoicechat.risk.service.server.ServerRoleService;
import fr.revoicechat.risk.web.api.RoleController;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed(ROLE_USER)
public class RoleControllerImpl implements RoleController {

  private final ServerRoleService serverRoleService;

  public RoleControllerImpl(final ServerRoleService serverRoleService) {
    this.serverRoleService = serverRoleService;
  }

  @Override
  @RisksMembershipData(risks = "UPDATE_ROLE", retriever = ServerRoleIdRetriever.class)
  public ServerRoleRepresentation updateRole(final UUID roleId, final CreatedServerRoleRepresentation representation) {
    return serverRoleService.update(roleId, representation);
  }

  @Override
  @RisksMembershipData(risks = "ADD_USER_ROLE", retriever = ServerRoleIdRetriever.class)
  public void addUserToRole(final UUID roleId, final List<UUID> users) {
    serverRoleService.addRoleToUser(roleId, users);
  }

  @Override
  @RisksMembershipData(risks = "ADD_USER_ROLE", retriever = ServerRoleIdRetriever.class)
  public void removeUserToRole(final UUID roleId, final List<UUID> users) {
    serverRoleService.removeUserToRole(roleId, users);
  }

  @Override
  @RisksMembershipData(risks = "UPDATE_ROLE", retriever = ServerRoleIdRetriever.class)
  public void patchOrAddRisk(final UUID roleId, final String type, RiskUpdateRepresentation updateRepresentation) {
    var risk = new DefaultRiskType(type);
    serverRoleService.addRiskOrReplace(roleId, updateRepresentation.entity(), risk, updateRepresentation.mode());
  }

  @Override
  public ServerRoleRepresentation getRole(final UUID roleId) {
    return serverRoleService.get(roleId);
  }
}
