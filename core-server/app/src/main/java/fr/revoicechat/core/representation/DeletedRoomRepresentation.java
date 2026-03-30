package fr.revoicechat.core.representation;

import java.util.UUID;

import fr.revoicechat.core.technicaldata.message.UnreadMessageStatus;

public record DeletedRoomRepresentation(UUID id) implements RoomRepresentation {

  @Override
  public String name() {
    return null;
  }

  @Override
  public UnreadMessageStatus unreadMessages() {
    return null;
  }
}
