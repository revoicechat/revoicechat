package fr.revoicechat.risk.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.risk.representation.ServerRoleRepresentation;
import fr.revoicechat.risk.service.membership.UserMembershipService;
import fr.revoicechat.risk.type.RiskType;
import fr.revoicechat.risk.web.api.UserMembershipController;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed(ROLE_USER)
public class UserMembershipControllerImpl implements UserMembershipController {

  private final UserMembershipService userMembershipService;

  public UserMembershipControllerImpl(final UserMembershipService userMembershipService) {this.userMembershipService = userMembershipService;}

  @Override
  public List<ServerRoleRepresentation> getMyRolesMembership() {
    return userMembershipService.getMyRolesMembership();
  }

  @Override
  public List<RiskType> getMyRiskType(final UUID serverId) {
    return userMembershipService.getMyRiskType(serverId);
  }
}
