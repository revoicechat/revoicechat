package fr.revoicechat.core.representation.message;

import java.util.UUID;

public record MessageMention(UUID id, String mentionName, boolean currentUserMentioned) implements TextPatternData {}