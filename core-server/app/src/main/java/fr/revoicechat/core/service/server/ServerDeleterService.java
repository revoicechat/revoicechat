package fr.revoicechat.core.service.server;

import java.util.UUID;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.repository.EmoteRepository;
import fr.revoicechat.core.repository.InvitationLinkRepository;
import fr.revoicechat.core.repository.RoomRepository;
import fr.revoicechat.core.repository.ServerRepository;
import fr.revoicechat.core.service.message.MessageDeleterService;
import fr.revoicechat.risk.service.server.ServerRolesDeleterService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class ServerDeleterService {

  private final EntityManager entityManager;
  private final ServerRepository serverRepository;
  private final RoomRepository roomRepository;
  private final InvitationLinkRepository invitationLinkRepository;
  private final EmoteRepository emoteRepository;
  private final ServerRolesDeleterService serverRolesDeleterService;
  private final MessageDeleterService messageDeleterService;

  public ServerDeleterService(EntityManager entityManager,
                              ServerRepository serverRepository,
                              RoomRepository roomRepository,
                              InvitationLinkRepository invitationLinkRepository,
                              EmoteRepository emoteRepository,
                              ServerRolesDeleterService serverRolesDeleterService,
                              MessageDeleterService messageDeleterService) {
    this.entityManager = entityManager;
    this.serverRepository = serverRepository;
    this.roomRepository = roomRepository;
    this.invitationLinkRepository = invitationLinkRepository;
    this.emoteRepository = emoteRepository;
    this.serverRolesDeleterService = serverRolesDeleterService;
    this.messageDeleterService = messageDeleterService;
  }

  public void delete(final UUID id) {
    var server = entityManager.find(Server.class, id);
    removeUserServer(server);
    removeRoom(server);
    removeLink(server);
    removeEmote(server);
    serverRolesDeleterService.delete(id);
    entityManager.remove(server);
  }

  private void removeRoom(final Server server) {
    roomRepository.findByServerId(server.getId()).forEach(room -> {
      messageDeleterService.deleteAllFrom(room);
      entityManager.remove(room);
    });
  }

  private void removeUserServer(final Server server) {
    serverRepository.getServerUser(server).forEach(entityManager::remove);
  }

  private void removeLink(final Server server) {
    invitationLinkRepository.getAllFromServer(server.getId())
                            .forEach(entityManager::remove);
  }

  private void removeEmote(final Server server) {
    emoteRepository.findByEntity(server.getId())
                   .forEach(entityManager::remove);
  }
}
