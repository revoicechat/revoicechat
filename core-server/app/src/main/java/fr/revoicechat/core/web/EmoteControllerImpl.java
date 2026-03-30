package fr.revoicechat.core.web;

import static fr.revoicechat.notification.data.NotificationActionType.*;
import static fr.revoicechat.security.utils.RevoiceChatRoles.*;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.notification.service.EmoteMediaNotifier;
import fr.revoicechat.core.representation.EmoteRepresentation;
import fr.revoicechat.core.service.emote.EmoteRetrieverService;
import fr.revoicechat.core.service.emote.EmoteUpdaterService;
import fr.revoicechat.core.service.server.ServerEntityService;
import fr.revoicechat.core.technicaldata.emote.NewEmote;
import fr.revoicechat.core.web.api.EmoteController;
import fr.revoicechat.risk.RisksMembershipData;
import fr.revoicechat.risk.retriever.ServerIdRetriever;
import fr.revoicechat.security.UserHolder;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.annotation.security.RolesAllowed;

public class EmoteControllerImpl implements EmoteController {

  private final UserHolder userHolder;
  private final ServerEntityService serverService;
  private final EmoteUpdaterService emoteUpdaterService;
  private final EmoteRetrieverService emoteRetrieverService;
  private final EmoteMediaNotifier emoteMediaNotifier;

  public EmoteControllerImpl(UserHolder userHolder, ServerEntityService serverService, EmoteUpdaterService emoteUpdaterService, EmoteRetrieverService emoteRetrieverService, final EmoteMediaNotifier emoteMediaNotifier) {
    this.userHolder = userHolder;
    this.serverService = serverService;
    this.emoteUpdaterService = emoteUpdaterService;
    this.emoteRetrieverService = emoteRetrieverService;
    this.emoteMediaNotifier = emoteMediaNotifier;
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public List<EmoteRepresentation> getMyEmotes() {
    var id = userHolder.getId();
    return Mapper.mapAll(emoteRetrieverService.getAll(id));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public EmoteRepresentation addToMyEmotes(final NewEmote emote) {
    var id = userHolder.getId();
    return Mapper.map(emoteUpdaterService.add(id, emote));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public List<EmoteRepresentation> getGlobalEmotes() {
    return Mapper.mapAll(emoteRetrieverService.getGlobal());
  }

  @Override
  @RolesAllowed(ROLE_ADMIN)
  public EmoteRepresentation addToGlobalEmotes(final NewEmote emote) {
    return Mapper.map(emoteUpdaterService.add(null, emote));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public List<EmoteRepresentation> getServerEmotes(final UUID serverId) {
    var server = serverService.getEntity(serverId);
    return Mapper.mapAll(emoteRetrieverService.getAll(server.getId()));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  @RisksMembershipData(risks = "ADD_EMOTE", retriever = ServerIdRetriever.class)
  public EmoteRepresentation addToServerEmotes(final UUID serverId, final NewEmote emote) {
    var server = serverService.getEntity(serverId);
    return Mapper.map(emoteUpdaterService.add(server.getId(), emote));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public EmoteRepresentation getEmote(final UUID id) {
    return Mapper.map(emoteRetrieverService.getEntity(id));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public EmoteRepresentation patchEmote(final UUID id, final NewEmote newEmote) {
    var emote = emoteUpdaterService.update(id, newEmote);
    emoteMediaNotifier.notify(emote, MODIFY);
    return Mapper.map(emote);
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public void deleteEmote(final UUID id) {
    var emote = emoteUpdaterService.delete(id);
    emoteMediaNotifier.notify(emote, REMOVE);
  }
}
