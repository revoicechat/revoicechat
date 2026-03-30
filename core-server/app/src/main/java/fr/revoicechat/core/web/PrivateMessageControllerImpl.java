package fr.revoicechat.core.web;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.notification.service.message.MessageNotifier;
import fr.revoicechat.core.repository.page.PageResult;
import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.core.representation.RoomRepresentation;
import fr.revoicechat.core.service.message.MessagePageResult;
import fr.revoicechat.core.service.message.MessageService;
import fr.revoicechat.core.service.room.PrivateMessageService;
import fr.revoicechat.core.technicaldata.message.MessageFilterParams;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.core.web.api.PrivateMessageController;
import fr.revoicechat.web.mapper.Mapper;

public class PrivateMessageControllerImpl implements PrivateMessageController {

  private final PrivateMessageService privateMessageService;
  private final MessageService messageService;
  private final MessagePageResult messagePageResult;
  private final MessageNotifier messageNotifier;

  public PrivateMessageControllerImpl(final PrivateMessageService privateMessageService, final MessageService messageService, final MessagePageResult messagePageResult, final MessageNotifier messageNotifier) {
    this.privateMessageService = privateMessageService;
    this.messageService = messageService;
    this.messagePageResult = messagePageResult;
    this.messageNotifier = messageNotifier;
  }

  @Override
  public List<RoomRepresentation> findAll() {
    return Mapper.mapAll(privateMessageService.findAll());
  }

  @Override
  public RoomRepresentation get(final UUID id) {
    return Mapper.map(privateMessageService.get(id));
  }

  @Override
  public PageResult<MessageRepresentation> messages(final UUID roomId, final MessageFilterParams params) {
    var pageResult = messagePageResult.getMessagesByRoom(roomId, params);
    return new PageResult<>(
        Mapper.mapAll(pageResult.content()),
        pageResult.size(),
        pageResult.totalElements()
    );
  }

  @Override
  public MessageRepresentation sendMessage(final UUID roomId, final NewMessage newMessage) {
    var message = messageService.create(roomId, newMessage);
    messageNotifier.add(message);
    return Mapper.map(message);
  }
}
