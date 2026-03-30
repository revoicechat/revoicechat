package fr.revoicechat.core.risk;

import fr.revoicechat.risk.type.RiskCategory;
import fr.revoicechat.risk.type.ServerEntityRiskType;

@RiskCategory("INVITATION_RISK_TYPE")
public enum InvitationRiskType implements ServerEntityRiskType {
  SERVER_INVITATION_ADD,
  SERVER_INVITATION_FETCH,
}
