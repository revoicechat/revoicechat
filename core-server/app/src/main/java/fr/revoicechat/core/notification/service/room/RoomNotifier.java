package fr.revoicechat.core.notification.service.room;

import static fr.revoicechat.core.notification.service.NotificationUserRetriever.findUserForRoom;
import static fr.revoicechat.notification.data.NotificationActionType.*;

import fr.revoicechat.core.model.room.Room;
import fr.revoicechat.core.notification.RoomNotification;
import fr.revoicechat.core.representation.DeletedRoomRepresentation;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class RoomNotifier {

  public void add(Room room) {
    notifyUpdate(new RoomNotification(Mapper.mapLight(room), ADD));
  }

  public void update(Room room) {
    notifyUpdate(new RoomNotification(Mapper.mapLight(room), MODIFY));
  }

  public void delete(Room room) {
    notifyUpdate(new RoomNotification(new DeletedRoomRepresentation(room.getId()), REMOVE));
  }

  private void notifyUpdate(final RoomNotification roomRepresentation) {
    Notification.of(roomRepresentation).sendTo(findUserForRoom(roomRepresentation.room().id()));
  }
}
