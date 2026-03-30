package fr.revoicechat.core.risk;

import fr.revoicechat.risk.type.RiskCategory;
import fr.revoicechat.risk.type.RoomEntityRiskType;

@RiskCategory("ROOM_RISK_TYPE")
public enum RoomRiskType implements RoomEntityRiskType {
  SERVER_ROOM_READ,
  SERVER_ROOM_SEND_MESSAGE,
  SERVER_ROOM_READ_MESSAGE,
}
