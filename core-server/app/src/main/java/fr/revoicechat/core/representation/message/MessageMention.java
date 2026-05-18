package fr.revoicechat.core.representation.message;

import java.util.UUID;

public record MessageMention(UUID id, MentionType type, String mentionName, boolean currentUserMentioned) {

  public enum MentionType {
    USER, ROLE,
  }
}