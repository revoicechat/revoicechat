package fr.revoicechat.risk.service.membership;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import fr.revoicechat.risk.model.RiskMode;
import fr.revoicechat.risk.model.UserRoleMembership;
import fr.revoicechat.risk.representation.RiskRepresentation;
import fr.revoicechat.risk.representation.ServerRoleRepresentation;
import fr.revoicechat.risk.service.server.ServerRoleService;
import fr.revoicechat.risk.type.RiskType;
import fr.revoicechat.security.UserHolder;

@ApplicationScoped
public class UserMembershipService {

  private final ServerRoleService serverRoleService;
  private final UserHolder userHolder;
  private final EntityManager entityManager;

  @Inject
  public UserMembershipService(ServerRoleService serverRoleService,
                               UserHolder userHolder,
                               EntityManager entityManager) {
    this.serverRoleService = serverRoleService;
    this.userHolder = userHolder;
    this.entityManager = entityManager;
  }

  public List<ServerRoleRepresentation> getMyRolesMembership() {
    var membership = entityManager.find(UserRoleMembership.class, userHolder.getId());
    return membership.getServerRoles().stream()
                     .map(serverRoleService::mapToRepresentation)
                     .toList();
  }

  public List<RiskType> getMyRiskType(final UUID serverId) {
    var risksList = getMyRolesMembership().stream()
                                          .filter(membership -> serverId.equals(membership.serverId()))
                                          .sorted(Comparator.comparing(ServerRoleRepresentation::priority))
                                          .map(ServerRoleRepresentation::risks)
                                          .flatMap(List::stream)
                                          .toList();
    List<RiskRepresentation> risks = new ArrayList<>();
    risksList.forEach(risk -> {
      if (risks.stream().noneMatch(r -> r.type().equals(risk.type()))) {
        risks.add(risk);
      }
    });
    return risks.stream().filter(r -> r.mode().equals(RiskMode.ENABLE))
                .map(RiskRepresentation::type)
                .toList();
  }
}
