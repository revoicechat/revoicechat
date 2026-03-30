package fr.revoicechat.core.service.message;

import static fr.revoicechat.risk.nls.RiskMembershipErrorCode.RISK_MEMBERSHIP_ERROR;

import java.util.Objects;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.model.room.PrivateMessageRoom;
import fr.revoicechat.core.model.room.Room;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.risk.service.RiskService;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import fr.revoicechat.risk.type.RiskType;
import fr.revoicechat.security.UserHolder;
import io.quarkus.security.UnauthorizedException;

@ApplicationScoped
public class MessageAuthorization {

  private final UserHolder userHolder;
  private final RiskService riskService;

  @Inject
  public MessageAuthorization(UserHolder userHolder, RiskService riskService) {
    this.userHolder = userHolder;
    this.riskService = riskService;
  }

  @Transactional
  public void asserRisk(final Message message, RiskType riskType) {
    User user = userHolder.get();
    if (Objects.equals(user, message.getUser())) {
      return;
    }
    Room room = message.getRoom();
    if (room instanceof ServerRoom serverRoom) {
      if (!riskService.hasRisk(user.getId(),
                               new RiskEntity(serverRoom.getServer().getId(), serverRoom.getId()),
                               riskType)) {
        throw new UnauthorizedException(RISK_MEMBERSHIP_ERROR.translate(riskType));
      }
    } else if (room instanceof PrivateMessageRoom) {
      throw new UnauthorizedException(RISK_MEMBERSHIP_ERROR.translate(riskType));
    }
  }
}
