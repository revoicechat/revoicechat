package fr.revoicechat.core.service.emote.risk;

import fr.revoicechat.core.model.Emote;
import fr.revoicechat.risk.service.RiskServiceImpl;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import fr.revoicechat.risk.type.RiskType;
import fr.revoicechat.security.model.AuthenticatedUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ServerEmoteRiskSupplier implements EmoteRiskSupplier {
  private final RiskServiceImpl riskServiceImpl;

  @Inject
  public ServerEmoteRiskSupplier(final RiskServiceImpl riskServiceImpl) {
    this.riskServiceImpl = riskServiceImpl;
  }

  @Override
  public boolean hasRisk(final Emote emote, final AuthenticatedUser user, final RiskType riskType) {
    return emote.getEntity() != null && riskServiceImpl.hasRisk(user.getId(), new RiskEntity(emote.getEntity(), null), riskType);
  }
}
