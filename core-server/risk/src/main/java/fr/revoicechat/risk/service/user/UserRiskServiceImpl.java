package fr.revoicechat.risk.service.user;

import static fr.revoicechat.risk.nls.RiskMembershipErrorCode.RISK_ABOVE_ERROR;

import java.util.UUID;

import fr.revoicechat.risk.service.AffectedRiskService;
import fr.revoicechat.risk.technicaldata.AffectedRisk;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import fr.revoicechat.risk.type.RiskType;
import io.quarkus.arc.Unremovable;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@Unremovable
@ApplicationScoped
public class UserRiskServiceImpl implements UserRiskService {

  private final AffectedRiskService affectedRiskService;

  public UserRiskServiceImpl(AffectedRiskService affectedRiskService) {
    this.affectedRiskService = affectedRiskService;
  }

  @Override
  @Transactional
  public void controlRiskPriority(int priority, UUID serverId, RiskType type) {
    var affectedRisk = affectedRiskService.get(new RiskEntity(serverId, null), type);
    var userMaxPriority = affectedRisk.map(AffectedRisk::priority).orElse(-1);
    if (userMaxPriority < priority) {
      throw new UnauthorizedException(RISK_ABOVE_ERROR.translate());
    }
  }
}
