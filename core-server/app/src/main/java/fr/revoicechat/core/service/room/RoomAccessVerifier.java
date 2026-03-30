package fr.revoicechat.core.service.room;

import static fr.revoicechat.core.risk.RoomRiskType.SERVER_ROOM_READ;

import java.util.UUID;

import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.moderation.model.SanctionType;
import fr.revoicechat.risk.service.RiskService;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import fr.revoicechat.security.UserHolder;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoomAccessVerifier {

  private final UserHolder userHolder;
  private final RiskService riskService;

  public RoomAccessVerifier(final UserHolder userHolder, final RiskService riskService) {
    this.userHolder = userHolder;
    this.riskService = riskService;}

  public boolean verify(ServerRoom room) {
    return verify(userHolder.getId(), room);
  }

  public boolean verify(UUID currentUserId, final ServerRoom room) {
    RiskEntity entity = new RiskEntity(room.getServer().getId(), room.getId());
    return riskService.hasRisk(currentUserId, entity, SERVER_ROOM_READ);
  }
}
