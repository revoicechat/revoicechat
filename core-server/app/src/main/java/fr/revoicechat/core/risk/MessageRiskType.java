package fr.revoicechat.core.risk;

import fr.revoicechat.risk.type.RiskCategory;
import fr.revoicechat.risk.type.RoomEntityRiskType;

@RiskCategory("MESSAGE_RISK_TYPE")
public enum MessageRiskType implements RoomEntityRiskType {
  MESSAGE_DELETE,
  MESSAGE_UPDATE,
}
