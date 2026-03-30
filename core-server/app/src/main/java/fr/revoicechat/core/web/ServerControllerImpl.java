package fr.revoicechat.core.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.*;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.notification.service.room.RoomNotifier;
import fr.revoicechat.core.notification.service.server.ServerUpdateNotifier;
import fr.revoicechat.core.representation.RoomRepresentation;
import fr.revoicechat.core.representation.ServerRepresentation;
import fr.revoicechat.core.representation.UserRepresentation;
import fr.revoicechat.core.service.room.RoomService;
import fr.revoicechat.core.service.server.ServerDeleterService;
import fr.revoicechat.core.service.server.ServerRetriever;
import fr.revoicechat.core.service.server.ServerService;
import fr.revoicechat.core.service.user.UserService;
import fr.revoicechat.core.technicaldata.room.NewRoom;
import fr.revoicechat.core.technicaldata.server.NewServer;
import fr.revoicechat.core.web.api.ServerController;
import fr.revoicechat.risk.RisksMembershipData;
import fr.revoicechat.risk.retriever.ServerIdRetriever;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.annotation.security.RolesAllowed;

public class ServerControllerImpl implements ServerController {

  private final ServerRetriever serverRetriever;
  private final ServerService serverService;
  private final RoomService roomService;
  private final UserService userService;
  private final ServerDeleterService serverDeleterService;
  private final RoomNotifier roomNotifier;
  private final ServerUpdateNotifier serverUpdateNotifier;

  public ServerControllerImpl(ServerRetriever serverRetriever, ServerService serverService, RoomService roomService, UserService userService, ServerDeleterService serverDeleterService, final RoomNotifier roomNotifier, final ServerUpdateNotifier serverUpdateNotifier) {
    this.serverRetriever = serverRetriever;
    this.serverService = serverService;
    this.roomService = roomService;
    this.userService = userService;
    this.serverDeleterService = serverDeleterService;
    this.roomNotifier = roomNotifier;
    this.serverUpdateNotifier = serverUpdateNotifier;
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public List<ServerRepresentation> getServers() {
    return Mapper.mapAll(serverRetriever.getAllMyServers());
  }

  @Override
  @RolesAllowed(ROLE_ADMIN)
  public List<ServerRepresentation> getAllServers() {
    return Mapper.mapAll(serverService.getAll());
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public List<ServerRepresentation> getPublicServers(boolean joinedToo) {
    return Mapper.mapAll(serverRetriever.getAllPublicServers(joinedToo));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public ServerRepresentation getServer(UUID id) {
    return Mapper.map(serverRetriever.getEntity(id));
  }

  @Override
  @RolesAllowed(ROLE_ADMIN)
  public ServerRepresentation createServer(NewServer representation) {
    return Mapper.map(serverService.create(representation));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  @RisksMembershipData(risks = "SERVER_UPDATE", retriever = ServerIdRetriever.class)
  public ServerRepresentation updateServer(UUID id, NewServer newServer) {
    var server = serverService.update(id, newServer);
    serverUpdateNotifier.update(server);
    return Mapper.map(server);
  }

  @Override
  @RolesAllowed(ROLE_USER)
  @RisksMembershipData(risks = "SERVER_DELETE", retriever = ServerIdRetriever.class)
  public void deleteServer(final UUID id) {
    serverDeleterService.delete(id);
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public List<RoomRepresentation> getRooms(UUID id) {
    return Mapper.mapAll(roomService.findAllForCurrentUser(id));
  }

  @Override
  @RolesAllowed(ROLE_USER)
  @RisksMembershipData(risks = "SERVER_ROOM_ADD", retriever = ServerIdRetriever.class)
  public RoomRepresentation createRoom(UUID id, NewRoom newRoom) {
    var room = roomService.create(id, newRoom);
    roomNotifier.add(room);
    return Mapper.map(room);
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public List<UserRepresentation> fetchUsers(final UUID id) {
    return Mapper.mapAll(userService.fetchUserForServer(id));
  }
}
