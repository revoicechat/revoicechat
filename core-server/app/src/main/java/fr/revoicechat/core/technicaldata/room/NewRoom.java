package fr.revoicechat.core.technicaldata.room;

import fr.revoicechat.core.model.room.RoomType;

public record NewRoom(
    String name,
    RoomType type
) {}
