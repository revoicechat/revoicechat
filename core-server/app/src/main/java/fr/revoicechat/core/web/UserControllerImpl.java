package fr.revoicechat.core.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.*;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.notification.service.message.MessageNotifier;
import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.core.representation.RoomRepresentation;
import fr.revoicechat.core.representation.UserRepresentation;
import fr.revoicechat.core.service.room.PrivateMessageService;
import fr.revoicechat.core.service.user.UserService;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.core.technicaldata.user.AdminUpdatableUserData;
import fr.revoicechat.core.technicaldata.user.UpdatableUserData;
import fr.revoicechat.core.web.api.UserController;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.annotation.security.RolesAllowed;

public class UserControllerImpl implements UserController {
  private final UserService userService;
  private final PrivateMessageService privateMessageService;
  private final MessageNotifier messageNotifier;

  public UserControllerImpl(final UserService userService, final PrivateMessageService privateMessageService, final MessageNotifier messageNotifier) {
    this.userService = userService;
    this.privateMessageService = privateMessageService;
    this.messageNotifier = messageNotifier;
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public UserRepresentation me() {
    return Mapper.map(userService.findCurrentUser());
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public UserRepresentation updateMe(final UpdatableUserData userData) {
    UserRepresentation representation = Mapper.map(userService.updateConnectedUser(userData));
    Notification.of(representation).sendTo(userService.everyone());
    return representation;
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public UserRepresentation get(UUID id) {
    return Mapper.map(userService.getUser(id));
  }

  @Override
  public RoomRepresentation getPrivateMessage(final UUID id) {
    return Mapper.map(privateMessageService.getDirectDiscussion(id));
  }

  @Override
  public MessageRepresentation sendPrivateMessage(final UUID id, final NewMessage newMessage) {
    var message = privateMessageService.sendPrivateMessageTo(id, newMessage);
    messageNotifier.add(message);
    return Mapper.map(message);
  }

  @Override
  @RolesAllowed(ROLE_ADMIN)
  public UserRepresentation updateAsAdmin(final UUID id, final AdminUpdatableUserData userData) {
    UserRepresentation representation = Mapper.map(userService.updateAsAdmin(id, userData));
    Notification.of(representation).sendTo(userService.everyone());
    return representation;
  }

  @Override
  @RolesAllowed(ROLE_ADMIN)
  public List<UserRepresentation> fetchAll() {
    return Mapper.mapAll(userService.fetchAll());
  }
}
