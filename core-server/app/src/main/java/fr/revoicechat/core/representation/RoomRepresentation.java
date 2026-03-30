package fr.revoicechat.core.representation;

import java.util.UUID;

import fr.revoicechat.core.technicaldata.message.UnreadMessageStatus;

public interface RoomRepresentation {
  UUID id();
  String name();
  UnreadMessageStatus unreadMessages();
}
