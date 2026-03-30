package fr.revoicechat.core.repository;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.model.room.PrivateMessageRoom;

public interface PrivateMessageRoomRepository {
  Stream<PrivateMessageRoom> findByUserId(UUID userId);

  PrivateMessageRoom getDirectDiscussion(UUID user1, UUID user2);
}
