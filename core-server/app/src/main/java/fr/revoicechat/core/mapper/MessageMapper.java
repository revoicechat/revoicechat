package fr.revoicechat.core.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.representation.EmoteRepresentation;
import fr.revoicechat.core.representation.MessageRepresentation;
import fr.revoicechat.core.representation.MessageRepresentation.MessageAnsweredRepresentation;
import fr.revoicechat.core.service.emote.EmoteRetrieverService;
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
  private final EmoteRetrieverService emoteService;

  public MessageMapper(final OpenGraphExtractor openGraphExtractor, final EmoteRetrieverService emoteService) {
    this.openGraphExtractor = openGraphExtractor;
    this.emoteService = emoteService;
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
        getEmoteRepresentations(message),
        message.getReactions().reactions(),
        openGraphExtractor.hasPreview(message.getText())
    );
  }

  private MessageAnsweredRepresentation toAnswerRepresentation(final Message repliedMessage) {
    if (repliedMessage == null) {
      return null;
    }
    return new MessageAnsweredRepresentation(
        repliedMessage.getId(),
        repliedMessage.getText(),
        !repliedMessage.getMediaDatas().isEmpty(),
        repliedMessage.getUser().getId(),
        getEmoteRepresentations(repliedMessage)
    );
  }

  private List<EmoteRepresentation> getEmoteRepresentations(final Message message) {
    Set<String> name = new HashSet<>();
    List<EmoteRepresentation> emotes = new ArrayList<>(
        distinctEmotes(name, Mapper.mapAll(emoteService.getGlobal()))
    );
    if (message.getRoom() instanceof ServerRoom serverRoom) {
      emotes.addAll(distinctEmotes(name, Mapper.mapAll(emoteService.getAll(serverRoom.getServer().getId()))));
    }
    emotes.addAll(distinctEmotes(name, Mapper.mapAll(emoteService.getAll(message.getUser().getId()))));
    return emotes;
  }

  private Collection<EmoteRepresentation> distinctEmotes(final Set<String> name, final List<EmoteRepresentation> all) {
    Collection<EmoteRepresentation> result = new ArrayList<>();
    for (EmoteRepresentation representation : all) {
      if (!name.contains(representation.name())) {
        result.add(representation);
        name.add(representation.name());
      }
    }
    return result;
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
