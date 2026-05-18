package fr.revoicechat.core.service.message.mension;

import static fr.revoicechat.core.representation.message.MessageMention.MentionType.USER;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.enterprise.context.ApplicationScoped;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.representation.message.MessageMention;
import fr.revoicechat.core.service.user.UserRetriever;
import fr.revoicechat.core.service.user.UserService;
import io.quarkus.arc.Unremovable;

@Unremovable
@ApplicationScoped
public class UserMentionExtractor implements MentionExtractor {

  private static final Pattern USER_MENTION = Pattern.compile("<@userId:(?<id>[0-9a-fA-F\\-]{36})>");

  private final UserRetriever userRetriever;
  private final UserService userService;

  public UserMentionExtractor(final UserRetriever userRetriever, final UserService userService) {
    this.userRetriever = userRetriever;
    this.userService = userService;}

  @Override
  public Map<String, MessageMention> extract(final Message message) {
    var currentUserId = userRetriever.currentUserId();
    Map<String, MessageMention> mentions = new HashMap<>();
    Matcher matcher = USER_MENTION.matcher(message.getText());
    while (matcher.find()) {
      toMention(matcher, currentUserId).ifPresent(m -> mentions.put(matcher.group(), m));
    }
    return mentions;
  }

  private Optional<MessageMention> toMention(Matcher matcher, final UUID currentUserId) {
    try {
      UUID id = UUID.fromString(matcher.group("id"));
      return Optional.ofNullable(userService.getUserOrNull(id))
                     .map(User::getDisplayName)
                     .map(name -> new MessageMention(id, USER, name, Objects.equals(currentUserId, id)));
    } catch (IllegalArgumentException _) {
      return Optional.empty();
    }
  }
}
