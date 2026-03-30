package fr.revoicechat.risk.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.risk.RisksMembershipData;
import fr.revoicechat.risk.representation.CreatedServerRoleRepresentation;
import fr.revoicechat.risk.representation.ServerRoleRepresentation;
import fr.revoicechat.risk.retriever.ServerIdRetriever;
import fr.revoicechat.risk.service.server.ServerRoleService;
import fr.revoicechat.risk.web.api.ServerRoleController;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed(ROLE_USER)
public class ServerRoleControllerImpl implements ServerRoleController {

  private final ServerRoleService serverRoleService;

  public ServerRoleControllerImpl(final ServerRoleService serverRoleService) {
    this.serverRoleService = serverRoleService;
  }

  @Override
  public List<ServerRoleRepresentation> getByServer(UUID serverId) {
    return serverRoleService.getByServer(serverId);
  }

  @Override
  @RisksMembershipData(risks = "ADD_ROLE", retriever = ServerIdRetriever.class)
  public ServerRoleRepresentation createRole(final UUID serverId, final CreatedServerRoleRepresentation representation) {
    return serverRoleService.create(serverId, representation);
  }
}
