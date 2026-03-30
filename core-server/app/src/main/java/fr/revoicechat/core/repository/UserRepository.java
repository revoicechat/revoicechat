package fr.revoicechat.core.repository;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.model.User;

public interface UserRepository {
  User findByLogin(String login);
  Stream<User> findByServers(UUID serverID);
  long count();
  Stream<User> everyone();

  Stream<User> findByRoom(UUID room);
}
