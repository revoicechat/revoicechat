package fr.revoicechat.live.voice.service;

import java.util.UUID;
import java.util.stream.Stream;

public interface ConnectedUserRetriever {

  /**
   * Retrieves all users currently connected to a specific voice room.
   *
   * @param room the room ID
   * @return stream of user IDs connected to the room
   */
  Stream<UUID> getConnectedUsers(UUID room);

  /**
   * Finds the room ID for a given user.
   *
   * @param user the user ID
   * @return the room ID the user is connected to, or null if not connected
   */
  UUID getRoomForUser(UUID user);
}
