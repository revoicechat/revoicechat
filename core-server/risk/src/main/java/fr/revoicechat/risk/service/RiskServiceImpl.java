package fr.revoicechat.risk.service;

import java.util.UUID;
import jakarta.inject.Singleton;

import fr.revoicechat.moderation.model.SanctionType;
import fr.revoicechat.moderation.service.SanctionService;
import fr.revoicechat.risk.model.RiskMode;
import fr.revoicechat.risk.technicaldata.AffectedRisk;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import fr.revoicechat.risk.type.RiskType;
import fr.revoicechat.security.UserHolder;
import io.quarkus.arc.Unremovable;

@Singleton
@Unremovable
public class RiskServiceImpl implements RiskService {

  private final SanctionService sanctionService;
  private final AffectedRiskService affectedRiskService;
  private final UserHolder userHolder;

  public RiskServiceImpl(SanctionService sanctionService, AffectedRiskService affectedRiskService, UserHolder userHolder) {
    this.sanctionService = sanctionService;
    this.affectedRiskService = affectedRiskService;
    this.userHolder = userHolder;
  }

  @Override
  public boolean hasRisk(final RiskEntity entity, final RiskType riskType) {
    return hasRisk(userHolder.get().getId(), entity, riskType, SanctionType.BAN);
  }

  @Override
  public boolean hasRisk(final RiskEntity entity, final RiskType riskType, final SanctionType sanctionType) {
    return hasRisk(userHolder.get().getId(), entity, riskType, sanctionType);
  }

  @Override
  public boolean hasRisk(final UUID userId, final RiskEntity entity, final RiskType riskType) {
    return hasRisk(userId, entity, riskType, SanctionType.BAN);
  }

  @Override
  public boolean hasRisk(final UUID userId, final RiskEntity entity, final RiskType riskType, final SanctionType sanctionType) {
    if (sanctionService.isSanctioned(userId, entity.serverId(), sanctionType)) {
      return false;
    }
    return affectedRiskService.get(userId, entity, riskType)
                              .map(AffectedRisk::mode)
                              .orElse(RiskMode.DISABLE)
                              .isEnable();
  }
}
