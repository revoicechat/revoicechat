package fr.revoicechat.core.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import java.util.UUID;

import fr.revoicechat.core.notification.service.message.MessageNotifier;
import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.core.representation.OpenGraphSchemaHolder;
import fr.revoicechat.core.service.message.MessageService;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.core.web.api.MessageController;
import fr.revoicechat.opengraph.OpenGraphSchema;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed(ROLE_USER)
public class MessageControllerImpl implements MessageController {

  private final MessageService messageService;
  private final MessageNotifier messageNotifier;

  public MessageControllerImpl(final MessageService messageService, final MessageNotifier messageNotifier) {
    this.messageService = messageService;
    this.messageNotifier = messageNotifier;
  }

  @Override
  public MessageRepresentation read(UUID id) {
    return Mapper.map(messageService.getMessage(id));
  }

  @Override
  public OpenGraphSchema getOpenGraph(final UUID id) {
    return Mapper.map(new OpenGraphSchemaHolder(messageService.getMessage(id)));
  }

  @Override
  public MessageRepresentation update(UUID id, NewMessage newMessage) {
    var message = messageService.update(id, newMessage);
    messageNotifier.update(message);
    return Mapper.map(message);
  }

  @Override
  public UUID delete(UUID id) {
    var message = messageService.delete(id);
    messageNotifier.delete(message);
    return message.getId();
  }

  @Override
  public MessageRepresentation addReaction(final UUID id, final String emoji) {
    var message = messageService.addReaction(id, emoji);
    messageNotifier.update(message);
    return Mapper.map(message);
  }
}
