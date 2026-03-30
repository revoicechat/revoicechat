package fr.revoicechat.core.technicaldata.message;

/**
 * @param hasUnreadMessage a room a some message that are unread
 * @param mentions         number of unread message that :
 *                         - replie to one of the user's message
 *                         - mention the user
 *                         - is a direct message
 */
public record UnreadMessageStatus(boolean hasUnreadMessage, long mentions) {

  public static UnreadMessageStatus none() {
    return new UnreadMessageStatus(false, 0);
  }

  public UnreadMessageStatus merge(final UnreadMessageStatus that) {
    return new UnreadMessageStatus(
        that.hasUnreadMessage() || this.hasUnreadMessage,
        this.mentions + that.mentions
    );
  }
}
