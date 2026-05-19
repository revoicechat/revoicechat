package fr.revoicechat.core.mapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.core.representation.MessageRepresentation.MessageAnsweredRepresentation;
import fr.revoicechat.core.representation.message.MessageMention;
import fr.revoicechat.core.representation.message.TextPattern;
import fr.revoicechat.core.service.message.textextractor.MessageTextPatternExtractor;
import fr.revoicechat.notification.data.UserNotificationRepresentation;
import fr.revoicechat.opengraph.OpenGraphExtractor;
import fr.revoicechat.web.mapper.Mapper;
import fr.revoicechat.web.mapper.RepresentationMapper;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
public class MessageMapper implements RepresentationMapper<Message, MessageRepresentation> {

  private final OpenGraphExtractor openGraphExtractor;
  private final MessageTextPatternExtractor messageTextPatternExtractor;

  public MessageMapper(OpenGraphExtractor openGraphExtractor, MessageTextPatternExtractor messageTextPatternExtractor) {
    this.openGraphExtractor = openGraphExtractor;
    this.messageTextPatternExtractor = messageTextPatternExtractor;
  }

  @Override
  public MessageRepresentation mapLight(final Message message) {
    return new MessageRepresentation(
        message.getId(),
        getServerId(message),
        message.getRoom().getId()
    );
  }

  @Override
  public MessageRepresentation map(final Message message) {
    var textPatterns = messageTextPatternExtractor.extract(message);
    return new MessageRepresentation(
        message.getId(),
        message.getText(),
        getServerId(message),
        message.getRoom().getId(),
        toAnswerRepresentation(message.getAnswerTo()),
        new UserNotificationRepresentation(message.getUser().getId(), message.getUser().getDisplayName()),
        message.getCreatedDate(),
        message.getUpdatedDate(),
        Mapper.mapAll(message.getMediaDatas()),
        message.getReactions().reactions(),
        textPatterns,
        isCurrentUserMentioned(textPatterns),
        openGraphExtractor.hasPreview(message.getText())
    );
  }

  private MessageAnsweredRepresentation toAnswerRepresentation(final Message repliedMessage) {
    if (repliedMessage == null) {
      return null;
    }
    var textPatterns = messageTextPatternExtractor.extract(repliedMessage);
    return new MessageAnsweredRepresentation(
        repliedMessage.getId(),
        repliedMessage.getText(),
        !repliedMessage.getMediaDatas().isEmpty(),
        repliedMessage.getUser().getId(),
        textPatterns
    );
  }

  private boolean isCurrentUserMentioned(final List<TextPattern> textPatterns) {
    return textPatterns.stream().map(TextPattern::data)
                       .filter(MessageMention.class::isInstance)
                       .map(MessageMention.class::cast)
                       .anyMatch(MessageMention::currentUserMentioned);
  }

  private UUID getServerId(final Message message) {
    return Optional.ofNullable(message.getRoom())
                   .filter(ServerRoom.class::isInstance)
                   .map(ServerRoom.class::cast)
                   .map(ServerRoom::getServer)
                   .map(Server::getId)
                   .orElse(null);
  }
}
