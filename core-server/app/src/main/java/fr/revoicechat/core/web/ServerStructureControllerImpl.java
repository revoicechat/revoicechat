package fr.revoicechat.core.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import java.util.UUID;

import fr.revoicechat.core.model.server.ServerStructure;
import fr.revoicechat.core.notification.service.server.ServerUpdateNotifier;
import fr.revoicechat.core.service.server.ServerEntityService;
import fr.revoicechat.core.service.server.ServerStructureService;
import fr.revoicechat.core.web.api.ServerStructureController;
import fr.revoicechat.risk.RisksMembershipData;
import fr.revoicechat.risk.retriever.ServerIdRetriever;
import jakarta.annotation.security.RolesAllowed;

public class ServerStructureControllerImpl implements ServerStructureController {
  private final ServerEntityService serverEntityService;
  private final ServerStructureService serverStructureService;
  private final ServerUpdateNotifier serverUpdateNotifier;

  public ServerStructureControllerImpl(ServerEntityService serverEntityService, ServerStructureService serverStructureService, final ServerUpdateNotifier serverUpdateNotifier) {
    this.serverEntityService = serverEntityService;
    this.serverStructureService = serverStructureService;
    this.serverUpdateNotifier = serverUpdateNotifier;
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public ServerStructure getStructure(final UUID id) {
    return serverStructureService.getStructure(id);
  }

  @Override
  @RolesAllowed(ROLE_USER)
  @RisksMembershipData(risks = "SERVER_ROOM_UPDATE", retriever = ServerIdRetriever.class)
  public ServerStructure patchStructure(final UUID id, final ServerStructure structure) {
    var newStructure = serverStructureService.updateStructure(id, structure);
    serverUpdateNotifier.update(serverEntityService.getEntity(id));
    return newStructure;
  }
}
