package fr.revoicechat.core.representation;

import java.util.UUID;

import fr.revoicechat.core.technicaldata.message.UnreadMessageStatus;

public record ServerRepresentation(
    UUID id,
    String name,
    UUID owner,
    UnreadMessageStatus unreadMessages,
    boolean canUpdate
) {}
