package fr.revoicechat.core.service.server;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.repository.ServerRepository;
import fr.revoicechat.security.UserHolder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ServerRetriever {

  private final UserHolder userHolder;
  private final ServerRepository serverRepository;
  private final ServerEntityRetriever serverEntityRetriever;

  public ServerRetriever(UserHolder userHolder, ServerRepository serverRepository, ServerEntityRetriever serverEntityRetriever) {
    this.userHolder = userHolder;
    this.serverRepository = serverRepository;
    this.serverEntityRetriever = serverEntityRetriever;
  }

  /** Retrieves a server from the database by its unique identifier. */
  public Server getEntity(final UUID id) {
    return serverEntityRetriever.getEntity(id);
  }

  /** @return all servers for the connected user. */
  @Transactional
  public List<Server> getAllMyServers() {
    return serverRepository.getByUser(userHolder.get()).toList();
  }

  /** @return all public servers. */
  @Transactional
  public List<Server> getAllPublicServers(final boolean joinedToo) {
    var servers = serverRepository.getPublicServer();
    if (joinedToo) {
      return servers.toList();
    } else {
      var serverIds = serverRepository.getByUser(userHolder.get()).map(Server::getId).toList();
      return servers.filter(server -> !serverIds.contains(server.getId())).toList();
    }
  }
}
