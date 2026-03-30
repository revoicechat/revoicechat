package fr.revoicechat.core.notification.service;

import fr.revoicechat.core.model.MediaData;
import fr.revoicechat.core.model.MediaOrigin;
import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.notification.MessageNotification;
import fr.revoicechat.core.notification.service.user.RoomUserFinder;
import fr.revoicechat.core.repository.MessageRepository;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.notification.data.NotificationActionType;
import fr.revoicechat.web.error.ResourceNotFoundException;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageMediaNotifier implements MediaNotifier {

  private final MessageRepository messageRepository;
  private final RoomUserFinder roomUserFinder;

  public MessageMediaNotifier(final MessageRepository messageRepository, final RoomUserFinder roomUserFinder) {
    this.messageRepository = messageRepository;
    this.roomUserFinder = roomUserFinder;
  }

  @Override
  public void notify(final MediaData mediaData, NotificationActionType actionType) {
    var message = messageRepository.findByMedia(mediaData.getId());
    if (message == null) {
      throw new ResourceNotFoundException(Message.class, mediaData.getId());
    }
    Notification.of(new MessageNotification(Mapper.map(message), actionType))
                .sendTo(roomUserFinder.find(message.getRoom().getId()));
  }

  @Override
  public MediaOrigin origin() {
    return MediaOrigin.ATTACHMENT;
  }
}
