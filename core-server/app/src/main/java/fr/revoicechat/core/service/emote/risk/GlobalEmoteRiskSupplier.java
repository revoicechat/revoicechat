package fr.revoicechat.core.service.emote.risk;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_ADMIN;

import fr.revoicechat.core.model.Emote;
import fr.revoicechat.risk.type.RiskType;
import fr.revoicechat.security.model.AuthenticatedUser;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GlobalEmoteRiskSupplier implements EmoteRiskSupplier {
  @Override
  public boolean hasRisk(final Emote emote, final AuthenticatedUser user, final RiskType riskType) {
    return emote.getEntity() == null && user.getRoles().contains(ROLE_ADMIN);
  }
}
