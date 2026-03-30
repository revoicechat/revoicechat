package fr.revoicechat.core.service.emote.risk;

import fr.revoicechat.core.model.Emote;
import fr.revoicechat.risk.type.RiskType;
import fr.revoicechat.security.model.AuthenticatedUser;

public interface EmoteRiskSupplier {
  boolean hasRisk(Emote emote, AuthenticatedUser user, RiskType riskType);
}