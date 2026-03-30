package fr.revoicechat.core.representation;

import java.util.UUID;

import fr.revoicechat.core.model.room.RoomType;
import fr.revoicechat.core.technicaldata.message.UnreadMessageStatus;

public record ServerRoomRepresentation(
    UUID id,
    String name,
    RoomType type,
    UUID serverId,
    UnreadMessageStatus unreadMessages
) implements RoomRepresentation {}
