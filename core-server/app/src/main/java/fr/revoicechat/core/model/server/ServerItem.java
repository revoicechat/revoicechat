package fr.revoicechat.core.model.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = ServerRoomItem.class, name = "ROOM"),
    @Type(value = ServerCategory.class, name = "CATEGORY")
})
public sealed interface ServerItem permits ServerRoomItem, ServerCategory {}
