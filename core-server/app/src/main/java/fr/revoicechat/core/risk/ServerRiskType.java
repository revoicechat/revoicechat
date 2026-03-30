package fr.revoicechat.core.risk;

import fr.revoicechat.risk.type.RiskCategory;
import fr.revoicechat.risk.type.ServerEntityRiskType;

@RiskCategory("SERVER_RISK_TYPE")
public enum ServerRiskType implements ServerEntityRiskType {
  SERVER_UPDATE,
  SERVER_DELETE,
  SERVER_ROOM_ADD,
  SERVER_ROOM_UPDATE,
  SERVER_ROOM_DELETE,
}
