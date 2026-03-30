package fr.revoicechat.risk.service;

import java.util.UUID;

import fr.revoicechat.moderation.model.SanctionType;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import fr.revoicechat.risk.type.RiskType;

public interface RiskService {

  boolean hasRisk(RiskEntity entity, RiskType riskType);

  boolean hasRisk(UUID userId, RiskEntity entity, RiskType riskType);

  boolean hasRisk(UUID userId, RiskEntity entity, RiskType riskType, SanctionType sanctionType);

  boolean hasRisk(RiskEntity entity, RiskType riskType, SanctionType sanctionType);
}
