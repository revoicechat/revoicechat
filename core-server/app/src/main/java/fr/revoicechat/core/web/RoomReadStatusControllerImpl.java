package fr.revoicechat.core.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import java.util.UUID;

import fr.revoicechat.core.service.room.RoomReadStatusService;
import fr.revoicechat.core.web.api.RoomReadStatusController;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed(ROLE_USER)
public class RoomReadStatusControllerImpl implements RoomReadStatusController {

  private final RoomReadStatusService roomReadStatusService;

  public RoomReadStatusControllerImpl(final RoomReadStatusService roomReadStatusService) {
    this.roomReadStatusService = roomReadStatusService;
  }

  @Override
  public void markAsRead(final UUID roomId, final UUID lastMessageId) {
    roomReadStatusService.update(roomId, lastMessageId);
  }
}
