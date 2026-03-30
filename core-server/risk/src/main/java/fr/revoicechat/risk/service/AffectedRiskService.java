package fr.revoicechat.risk.service;

import java.util.Optional;
import java.util.UUID;

import fr.revoicechat.risk.technicaldata.AffectedRisk;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import fr.revoicechat.risk.type.RiskType;

public interface AffectedRiskService {

  Optional<AffectedRisk> get(RiskEntity entity, RiskType riskType);

  Optional<AffectedRisk> get(UUID userId, RiskEntity entity, RiskType riskType);
}
