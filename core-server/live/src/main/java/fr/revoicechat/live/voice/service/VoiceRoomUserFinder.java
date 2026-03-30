package fr.revoicechat.live.voice.service;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.notification.model.NotificationRegistrable;

public interface VoiceRoomUserFinder {

  /**
   * @param room currently, the user has no server, so no rooms.
   *             so we cannot know the user by room.
   */
  Stream<NotificationRegistrable> find(UUID room);
}