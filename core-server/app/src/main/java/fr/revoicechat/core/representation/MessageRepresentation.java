package fr.revoicechat.core.representation;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.model.MessageReactions.MessageReaction;
import fr.revoicechat.notification.data.UserNotificationRepresentation;

public record MessageRepresentation(
    UUID id,
    String text,
    UUID serverId,
    UUID roomId,
    MessageAnsweredRepresentation answeredTo,
    UserNotificationRepresentation user,
    OffsetDateTime createdDate,
    OffsetDateTime updatedDate,
    List<MediaDataRepresentation> medias,
    List<EmoteRepresentation> emotes,
    List<MessageReaction> reactions,
    boolean messageUrlPreview
) {

  public MessageRepresentation(UUID id, UUID serverId, UUID roomId) {
    this(id, null, serverId, roomId, null, null, null, null, null, null, null, false);
  }

  public record MessageAnsweredRepresentation(
      UUID id,
      String text,
      boolean hasMedias,
      UUID userId,
      List<EmoteRepresentation> emotes
  ) {}
}
