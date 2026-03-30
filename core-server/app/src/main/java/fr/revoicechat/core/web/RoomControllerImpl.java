package fr.revoicechat.core.web;

import static fr.revoicechat.moderation.model.SanctionType.TEXT_TIME_OUT;
import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import java.util.UUID;
import jakarta.annotation.security.RolesAllowed;

import fr.revoicechat.core.notification.service.message.MessageNotifier;
import fr.revoicechat.core.notification.service.room.RoomNotifier;
import fr.revoicechat.core.repository.page.PageResult;
import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.core.representation.RoomPresenceRepresentation;
import fr.revoicechat.core.representation.RoomRepresentation;
import fr.revoicechat.core.retriever.EntityByRoomIdRetriever;
import fr.revoicechat.core.service.message.MessagePageResult;
import fr.revoicechat.core.service.message.MessageService;
import fr.revoicechat.core.service.room.RoomService;
import fr.revoicechat.core.technicaldata.message.MessageFilterParams;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.core.technicaldata.room.NewRoom;
import fr.revoicechat.core.technicaldata.room.RoomPresence;
import fr.revoicechat.core.web.api.RoomController;
import fr.revoicechat.risk.RisksMembershipData;
import fr.revoicechat.web.mapper.Mapper;

@RolesAllowed(ROLE_USER)
public class RoomControllerImpl implements RoomController {

  private final RoomService roomService;
  private final MessageService messageService;
  private final MessagePageResult messagePageResult;
  private final RoomNotifier roomNotifier;
  private final MessageNotifier messageNotifier;

  public RoomControllerImpl(RoomService roomService,
                            MessageService messageService,
                            MessagePageResult messagePageResult,
                            RoomNotifier roomNotifier,
                            MessageNotifier messageNotifier) {
    this.roomService = roomService;
    this.messageService = messageService;
    this.messagePageResult = messagePageResult;
    this.roomNotifier = roomNotifier;
    this.messageNotifier = messageNotifier;
  }

  @Override
  @RisksMembershipData(risks = "SERVER_ROOM_READ", retriever = EntityByRoomIdRetriever.class)
  public RoomRepresentation read(UUID roomId) {
    return Mapper.map(roomService.getRoom(roomId));
  }

  @Override
  @RisksMembershipData(risks = "SERVER_ROOM_UPDATE", retriever = EntityByRoomIdRetriever.class)
  public RoomRepresentation update(UUID roomId, NewRoom newRoom) {
    var room = roomService.update(roomId, newRoom);
    roomNotifier.update(room);
    return Mapper.map(room);
  }

  @Override
  @RisksMembershipData(risks = "SERVER_ROOM_DELETE", retriever = EntityByRoomIdRetriever.class)
  public UUID delete(UUID roomId) {
    var room = roomService.delete(roomId);
    roomNotifier.delete(room);
    return room.getId();
  }


  @Override
  @RisksMembershipData(risks = "SERVER_ROOM_READ_MESSAGE", retriever = EntityByRoomIdRetriever.class)
  public PageResult<MessageRepresentation> messages(UUID roomId, MessageFilterParams params) {
    var pageResult = messagePageResult.getMessagesByRoom(roomId, params);
    return new PageResult<>(
        Mapper.mapAll(pageResult.content()),
        pageResult.size(),
        pageResult.totalElements()
    );
  }

  @Override
  @RisksMembershipData(risks = "SERVER_ROOM_SEND_MESSAGE", retriever = EntityByRoomIdRetriever.class, sanctionType = TEXT_TIME_OUT)
  public MessageRepresentation sendMessage(UUID roomId, NewMessage newMessage) {
    var message = messageService.create(roomId, newMessage);
    messageNotifier.add(message);
    return Mapper.map(message);
  }

  @Override
  public RoomPresenceRepresentation fetchUsers(final UUID id) {
    return Mapper.map(new RoomPresence(roomService.getRoom(id)));
  }
}
