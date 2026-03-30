package fr.revoicechat.core.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.notification.NewUserInServer;
import fr.revoicechat.core.representation.InvitationRepresentation;
import fr.revoicechat.core.service.invitation.InvitationLinkService;
import fr.revoicechat.core.service.server.ServerJoiner;
import fr.revoicechat.core.technicaldata.invitation.InvitationCategory;
import fr.revoicechat.core.web.api.ServerInvitationController;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.risk.RisksMembershipData;
import fr.revoicechat.risk.retriever.ServerIdRetriever;
import fr.revoicechat.risk.service.user.UserServerFinder;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.annotation.security.RolesAllowed;

public class ServerInvitationControllerImpl implements ServerInvitationController {

  private final InvitationLinkService invitationLinkService;
  private final ServerJoiner serverJoiner;
  private final UserServerFinder userServerFinder;

  public ServerInvitationControllerImpl(InvitationLinkService invitationLinkService,
                                        ServerJoiner serverJoiner,
                                        UserServerFinder userServerFinder) {
    this.invitationLinkService = invitationLinkService;
    this.serverJoiner = serverJoiner;
    this.userServerFinder = userServerFinder;
  }

  @Override
  @RolesAllowed(ROLE_USER)
  @RisksMembershipData(risks = "SERVER_INVITATION_ADD", retriever = ServerIdRetriever.class)
  public InvitationRepresentation generateServerInvitation(final UUID id, final String category) {
    return Mapper.map(invitationLinkService.generateServerInvitation(id, InvitationCategory.of(category)));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  @RisksMembershipData(risks = "SERVER_INVITATION_FETCH", retriever = ServerIdRetriever.class)
  public List<InvitationRepresentation> getAllServerInvitations(final UUID id) {
    return Mapper.mapAll(invitationLinkService.getAllServerInvitations(id));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public void joinPublic(final UUID serverId) {
    var userServer = serverJoiner.joinPublic(serverId);
    Notification.of(new NewUserInServer(userServer.getServer().getId(), userServer.getUser().getId()))
                .sendTo(userServerFinder.findUserForServer(userServer.getServer().getId()));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public void joinPrivate(final UUID invitation) {
    var userServer = serverJoiner.joinPrivate(invitation);
    Notification.of(new NewUserInServer(userServer.getServer().getId(), userServer.getUser().getId()))
                .sendTo(userServerFinder.findUserForServer(userServer.getServer().getId()));
  }
}
