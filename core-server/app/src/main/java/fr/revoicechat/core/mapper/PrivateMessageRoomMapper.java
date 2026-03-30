package fr.revoicechat.core.mapper;

import fr.revoicechat.core.model.room.PrivateMessageRoom;
import fr.revoicechat.core.representation.PrivateMessageRoomRepresentation;
import fr.revoicechat.core.service.room.RoomReadStatusService;
import fr.revoicechat.web.mapper.Mapper;
import fr.revoicechat.web.mapper.RepresentationMapper;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
public class PrivateMessageRoomMapper implements RepresentationMapper<PrivateMessageRoom, PrivateMessageRoomRepresentation> {

  private final RoomReadStatusService roomReadStatusService;

  public PrivateMessageRoomMapper(final RoomReadStatusService roomReadStatusService) {
    this.roomReadStatusService = roomReadStatusService;
  }

  @Override
  public PrivateMessageRoomRepresentation map(final PrivateMessageRoom room) {
    return new PrivateMessageRoomRepresentation(
        room.getId(),
        room.getName(),
        room.getMode(),
        Mapper.mapAll(room.getUsers()),
        roomReadStatusService.getUnreadMessagesStatus(room)
    );
  }

  @Override
  public PrivateMessageRoomRepresentation mapLight(final PrivateMessageRoom room) {
    return new PrivateMessageRoomRepresentation(
        room.getId(),
        room.getName(),
        room.getMode(),
        Mapper.mapLightAll(room.getUsers()),
        null
    );
  }
}
