package fr.revoicechat.core.repository;

import java.util.List;
import java.util.stream.Stream;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.ServerUser;
import fr.revoicechat.core.model.User;

public interface ServerRepository {
  List<Server> findAll();

  Stream<Server> getByUser(User user);

  Stream<ServerUser> getServerUser(Server server);

  Stream<Server> getPublicServer();
}
