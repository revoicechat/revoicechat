package fr.revoicechat.core.mapper;

import java.util.Optional;
import java.util.UUID;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.representation.ServerRoomRepresentation;
import fr.revoicechat.core.service.room.RoomReadStatusService;
import fr.revoicechat.web.mapper.RepresentationMapper;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
public class ServerRoomMapper implements RepresentationMapper<ServerRoom, ServerRoomRepresentation> {

  private final RoomReadStatusService roomReadStatusService;

  public ServerRoomMapper(final RoomReadStatusService roomReadStatusService) {
    this.roomReadStatusService = roomReadStatusService;
  }

  @Override
  public ServerRoomRepresentation map(final ServerRoom room) {
    return new ServerRoomRepresentation(
        room.getId(),
        room.getName(),
        room.getType(),
        getServerId(room),
        roomReadStatusService.getUnreadMessagesStatus(room)
    );
  }

  @Override
  public ServerRoomRepresentation mapLight(final ServerRoom room) {
    return new ServerRoomRepresentation(
        room.getId(),
        room.getName(),
        room.getType(),
        getServerId(room),
        null
    );
  }

  private static UUID getServerId(final ServerRoom room) {
    return Optional.ofNullable(room)
                   .map(ServerRoom::getServer)
                   .map(Server::getId)
                   .orElse(null);
  }
}
