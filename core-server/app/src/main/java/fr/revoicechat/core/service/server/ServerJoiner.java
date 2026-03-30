package fr.revoicechat.core.service.server;

import static fr.revoicechat.core.model.InvitationType.SERVER_JOIN;
import static fr.revoicechat.core.nls.ServerErrorCode.*;

import java.util.UUID;

import fr.revoicechat.core.model.InvitationLink;
import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.ServerUser;
import fr.revoicechat.core.service.invitation.InvitationLinkEntityRetriever;
import fr.revoicechat.core.service.invitation.InvitationLinkUsage;
import fr.revoicechat.core.service.serveruser.ServerUserService;
import fr.revoicechat.web.error.BadRequestException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

/**
 * Service responsible for rejoining a {@link Server}.
 */
@ApplicationScoped
public class ServerJoiner {

  private final ServerEntityRetriever serverEntityRetriever;
  private final InvitationLinkEntityRetriever invitationLinkService;
  private final ServerUserService serverUserService;
  private final InvitationLinkUsage invitationLinkUsage;

  public ServerJoiner(ServerEntityRetriever serverEntityRetriever,
                      InvitationLinkEntityRetriever invitationLinkService,
                      ServerUserService serverUserService,
                      InvitationLinkUsage invitationLinkUsage) {
    this.serverEntityRetriever = serverEntityRetriever;
    this.invitationLinkService = invitationLinkService;
    this.serverUserService = serverUserService;
    this.invitationLinkUsage = invitationLinkUsage;
  }

  @Transactional
  public ServerUser joinPublic(final UUID serverId) {
    var server = serverEntityRetriever.getEntity(serverId);
    if (!server.isPublic()) {
      throw new BadRequestException(SERVER_NOT_PUBLIC);
    }
    return serverUserService.join(server);
  }

  @Transactional
  public ServerUser joinPrivate(final UUID invitation) {
    var invitationLink = invitationLinkService.getEntity(invitation);
    if (!isValideInvitation(invitationLink)) {
      throw new BadRequestException(NO_VALID_INVITATION);
    }
    var serverUser = serverUserService.join(invitationLink.getTargetedServer());
    invitationLinkUsage.use(invitationLink);
    return serverUser;
  }

  private static boolean isValideInvitation(InvitationLink invitationLink) {
    return invitationLink != null
           && SERVER_JOIN.equals(invitationLink.getType())
           && invitationLink.isValid();
  }
}
