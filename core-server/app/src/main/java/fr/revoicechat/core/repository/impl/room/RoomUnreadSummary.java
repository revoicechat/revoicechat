package fr.revoicechat.core.repository.impl.room;

import java.util.UUID;

import fr.revoicechat.core.technicaldata.message.UnreadMessageStatus;

public record RoomUnreadSummary(
    UUID firstUnreadMessage,
    long numberOfUnreadMessage,
    long numberOfUnreadAnswerToMe,
    long numberOfUnreadAnswerMention
) {
  public UnreadMessageStatus toUnreadMessageStatus() {
    return new UnreadMessageStatus(
        this.numberOfUnreadMessage != 0,
        this.numberOfUnreadAnswerToMe + this.numberOfUnreadAnswerMention
    );
  }
}
