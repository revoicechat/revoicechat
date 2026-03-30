package fr.revoicechat.core.representation;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.model.room.PrivateMessageMode;
import fr.revoicechat.core.technicaldata.message.UnreadMessageStatus;

public record PrivateMessageRoomRepresentation(
    UUID id,
    String name,
    PrivateMessageMode mode,
    List<UserRepresentation> users,
    UnreadMessageStatus unreadMessages
) implements RoomRepresentation {}
