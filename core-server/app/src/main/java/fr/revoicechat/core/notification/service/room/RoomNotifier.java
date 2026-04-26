package fr.revoicechat.core.notification.service.room;

import static fr.revoicechat.core.notification.service.NotificationUserRetriever.findUserForRoom;
import static fr.revoicechat.notification.data.NotificationActionType.*;

import fr.revoicechat.core.model.room.Room;
import fr.revoicechat.core.notification.RoomNotification;
import fr.revoicechat.core.representation.DeletedRoomRepresentation;
import fr.revoicechat.core.representation.RoomRepresentation;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.notification.data.NotificationActionType;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class RoomNotifier {

  public void add(Room room) {
    notifyUpdate(Mapper.mapLight(room), ADD, room);
  }

  public void update(Room room) {
    notifyUpdate(Mapper.mapLight(room), MODIFY, room);
  }

  public void delete(Room room) {
    notifyUpdate(new DeletedRoomRepresentation(room.getId()), REMOVE, room);
  }

  private void notifyUpdate(RoomRepresentation representation, NotificationActionType type, Room room) {
    Notification.of(new RoomNotification(representation, type)).sendTo(findUserForRoom(room));
  }
}
