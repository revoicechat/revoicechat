package fr.revoicechat.risk.service.user;

import java.util.UUID;

import fr.revoicechat.risk.type.RiskType;

public interface UserRiskService {

  /** throw an error if a user has a priority under the targeted priority */
  void controlRiskPriority(int priority, UUID serverId, RiskType type);
}
