package fr.revoicechat.risk.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fr.revoicechat.risk.model.RiskMode;
import fr.revoicechat.risk.model.ServerRoles;
import fr.revoicechat.risk.repository.ServerRolesRepository;
import fr.revoicechat.risk.technicaldata.AffectedRisk;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import fr.revoicechat.risk.type.RiskType;
import fr.revoicechat.security.UserHolder;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
public class AffectedRiskServiceImpl implements AffectedRiskService {

  private final ServerRolesRepository serverRolesRepository;
  private final UserHolder userHolder;

  public AffectedRiskServiceImpl(ServerRolesRepository serverRolesRepository, UserHolder userHolder) {
    this.serverRolesRepository = serverRolesRepository;
    this.userHolder = userHolder;
  }

  @Override
  public Optional<AffectedRisk> get(final RiskEntity entity, final RiskType riskType) {
    return get(userHolder.getId(), entity, riskType);
  }

  @Override
  public Optional<AffectedRisk> get(final UUID userId, final RiskEntity entity, final RiskType riskType) {
    if (serverRolesRepository.isOwner(entity.serverId(), userId)) {
      return Optional.of(new AffectedRisk(
          null,
          RiskMode.ENABLE,
          null,
          Integer.MAX_VALUE
      ));
    }
    List<ServerRoles> membership = serverRolesRepository.getServerRoles(userId);
    if (membership.isEmpty()) {
      return Optional.empty();
    }
    return serverRolesRepository.getAffectedRisks(entity, riskType)
                                .sorted()
                                .filter(affectedRisk -> isUserMembership(affectedRisk, membership))
                                .filter(affectedRisk -> !affectedRisk.mode().equals(RiskMode.DEFAULT))
                                .findFirst();
  }

  private boolean isUserMembership(final AffectedRisk affectedRisk, final List<ServerRoles> membership) {
    return membership.stream().anyMatch(role -> role.getId().equals(affectedRisk.role()));
  }
}
