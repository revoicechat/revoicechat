package fr.revoicechat.core.risk;

import fr.revoicechat.risk.type.RiskCategory;
import fr.revoicechat.risk.type.ServerEntityRiskType;

@RiskCategory("EMOTE_RISK_TYPE")
public enum EmoteRiskType implements ServerEntityRiskType {
  ADD_EMOTE,
  UPDATE_EMOTE,
  REMOVE_EMOTE,
}
