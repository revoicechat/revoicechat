package fr.revoicechat.core.service.message.textextractor;

import static fr.revoicechat.core.representation.message.PatternType.EMOTE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.revoicechat.core.model.Emote;
import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.representation.EmoteRepresentation;
import fr.revoicechat.core.representation.message.TextPattern;
import fr.revoicechat.core.service.emote.EmoteRetrieverService;
import fr.revoicechat.web.mapper.Mapper;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
class EmoteExtractor implements TextPatternExtractor {

  private static final Pattern EMOTE_REGEXP = Pattern.compile(":(?<id>[A-Za-z0-9\\-_]+):");

  private final EmoteRetrieverService emoteService;

  EmoteExtractor(EmoteRetrieverService emoteService) {
    this.emoteService = emoteService;
  }

  @Override
  public List<TextPattern> extract(final Message message) {
    List<String> emoteIds = getEmotesContent(message);
    if (emoteIds.isEmpty()) {
      return List.of();
    }
    return emotes(message).stream()
                          .filter(emote -> emoteIds.contains(emote.getContent()))
                          .map(emote -> (EmoteRepresentation) Mapper.map(emote))
                          .map(emote -> new TextPattern(":%s:".formatted(emote.name()), EMOTE, emote))
                          .toList();
  }

  private List<String> getEmotesContent(final Message message) {
    List<String> emoteIds = new ArrayList<>();
    Matcher matcher = EMOTE_REGEXP.matcher(message.getText());
    while (matcher.find()) {
      try {
        emoteIds.add(matcher.group("id"));
      } catch (IllegalArgumentException _) {/* ignored */}
    }
    return emoteIds;
  }

  private List<Emote> emotes(final Message message) {
    Set<String> name = new HashSet<>();
    List<Emote> emotes = new ArrayList<>(distinctEmotes(name, emoteService.getGlobal()));
    if (message.getRoom() instanceof ServerRoom serverRoom) {
      emotes.addAll(distinctEmotes(name, emoteService.getAll(serverRoom.getServer().getId())));
    }
    emotes.addAll(distinctEmotes(name, emoteService.getAll(message.getUser().getId())));
    return emotes;
  }

  private Collection<Emote> distinctEmotes(final Set<String> name, final List<Emote> all) {
    Collection<Emote> result = new ArrayList<>();
    for (Emote emote : all) {
      if (!name.contains(emote.getContent())) {
        result.add(emote);
        name.add(emote.getContent());
      }
    }
    return result;
  }
}
