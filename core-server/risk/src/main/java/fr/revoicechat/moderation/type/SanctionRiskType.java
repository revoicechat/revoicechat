package fr.revoicechat.moderation.type;

import fr.revoicechat.risk.type.RiskCategory;
import fr.revoicechat.risk.type.ServerEntityRiskType;

@RiskCategory("SANCTION_RISK_TYPE")
public enum SanctionRiskType implements ServerEntityRiskType {
  TOGGLE_SANCTION,
  REVOKE_SANCTION,
}
