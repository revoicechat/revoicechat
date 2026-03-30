package fr.revoicechat.core.notification.service.message;

import static fr.revoicechat.core.notification.service.NotificationUserRetriever.findUserForRoom;
import static fr.revoicechat.notification.data.NotificationActionType.*;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.notification.MessageNotification;
import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class MessageNotifier {
  public void add(Message message) {
    MessageRepresentation representation = Mapper.map(message);
    notifyUpdate(new MessageNotification(representation, ADD));
  }

  public void update(Message message) {
    MessageRepresentation representation = Mapper.map(message);
    notifyUpdate(new MessageNotification(representation, MODIFY));
  }

  public void delete(Message message) {
    MessageRepresentation representation = Mapper.mapLight(message);
    notifyUpdate(new MessageNotification(representation, REMOVE));
  }

  private void notifyUpdate(final MessageNotification message) {
    Notification.of(message).sendTo(findUserForRoom(message.message().roomId()));
  }
}
