package fr.revoicechat.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Users quick respond to messages using emojis.
 * This lightweight interaction improves engagement,
 * reduces noise from short replies, and provides instant feedback
 * without interrupting conversation flow.
 */
public record MessageReactions(List<MessageReaction> reactions) {

  public MessageReactions toggle(final String emoji, final UUID user) {
    return getReaction(emoji).map(MessageReaction::users)
                             .filter(users -> users.contains(user))
                             .isPresent()
           ? remove(emoji, user)
           : add(emoji, user);
  }

  private MessageReactions add(final String emoji, final UUID user) {
    var reactions = new ArrayList<>(reactions());
    getReaction(emoji).ifPresentOrElse(
        reaction -> {
          reactions.remove(reaction);
          var updatedUsers = new ArrayList<>(reaction.users);
          updatedUsers.add(user);
          reactions.add(new MessageReaction(emoji, updatedUsers));
        },
        () -> {
          List<UUID> users = new ArrayList<>();
          users.add(user);
          reactions.add(new MessageReaction(emoji, users));
        }
    );
    return new MessageReactions(reactions);
  }

  private MessageReactions remove(final String emoji, final UUID user) {
    var reactions = new ArrayList<>(reactions());
    var reaction = getReaction(emoji).orElseThrow();
    reactions.remove(reaction);
    var updatedUsers = new ArrayList<>(reaction.users);
    updatedUsers.remove(user);
    if (!updatedUsers.isEmpty()) {
      reactions.add(new MessageReaction(emoji, updatedUsers));
    }
    return new MessageReactions(reactions);
  }

  private Optional<MessageReaction> getReaction(final String emoji) {
    return reactions().stream()
                      .filter(react -> Objects.equals(react.emoji, emoji))
                      .findFirst();
  }

  /**
   * @param emoji It can be an emoji (🥖👽🍟...), or a UUID referring to an {@link Emote}
   * @param users all user that reacted with this specific emoji
   */
  public record MessageReaction(
      String emoji,
      List<UUID> users
  ) {}
}
