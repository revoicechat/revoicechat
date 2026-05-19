package fr.revoicechat.core.service.message.textextractor;

import static fr.revoicechat.core.representation.message.PatternType.USER_MENTION;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.representation.message.MessageMention;
import fr.revoicechat.core.representation.message.TextPattern;
import fr.revoicechat.core.service.user.UserRetriever;
import fr.revoicechat.core.service.user.UserService;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
public class UserMentionExtractor implements TextPatternExtractor {

  private static final Pattern USER_MENTION_REGEXP = Pattern.compile("<@userId:(?<id>[0-9a-fA-F\\-]{36})>");

  private final UserRetriever userRetriever;
  private final UserService userService;

  public UserMentionExtractor(final UserRetriever userRetriever, final UserService userService) {
    this.userRetriever = userRetriever;
    this.userService = userService;}

  @Override
  public List<TextPattern> extract(final Message message) {
    var currentUserId = userRetriever.currentUserId();
    List<TextPattern> mentions = new ArrayList<>();
    Matcher matcher = USER_MENTION_REGEXP.matcher(message.getText());
    while (matcher.find()) {
      toMention(matcher, currentUserId).ifPresent(mentions::add);
    }
    return mentions;
  }

  private Optional<TextPattern> toMention(Matcher matcher, final UUID currentUserId) {
    try {
      UUID id = UUID.fromString(matcher.group("id"));
      return Optional.ofNullable(userService.getUserOrNull(id))
                     .map(User::getDisplayName)
                     .map(name -> new MessageMention(id, name, Objects.equals(currentUserId, id)))
                     .map(mention -> new TextPattern(matcher.group(), USER_MENTION, mention));
    } catch (IllegalArgumentException _) {
      return Optional.empty();
    }
  }
}
