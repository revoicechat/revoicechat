package fr.revoicechat.core.mapper;

import static fr.revoicechat.core.risk.ServerRiskType.SERVER_UPDATE;

import java.util.Optional;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.representation.ServerRepresentation;
import fr.revoicechat.core.service.room.RoomReadStatusService;
import fr.revoicechat.risk.service.RiskService;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import fr.revoicechat.web.mapper.RepresentationMapper;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
public class ServerMapper implements RepresentationMapper<Server, ServerRepresentation> {

  private final RiskService riskService;
  private final RoomReadStatusService roomReadStatusService;

  public ServerMapper(final RiskService riskService, final RoomReadStatusService roomReadStatusService) {
    this.riskService = riskService;
    this.roomReadStatusService = roomReadStatusService;
  }

  @Override
  public ServerRepresentation map(final Server server) {
    return new ServerRepresentation(
        server.getId(),
        server.getName(),
        Optional.ofNullable(server.getOwner()).map(User::getId).orElse(null),
        roomReadStatusService.getUnreadMessagesStatus(server),
        riskService.hasRisk(new RiskEntity(server.getId(), null), SERVER_UPDATE)
    );
  }

  @Override
  public ServerRepresentation mapLight(final Server server) {
    return new ServerRepresentation(
        server.getId(),
        server.getName(),
        Optional.ofNullable(server.getOwner()).map(User::getId).orElse(null),
        null,
        false
    );
  }
}
