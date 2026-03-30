package fr.revoicechat.core.service.server;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.ServerType;
import fr.revoicechat.core.model.ServerUser;
import fr.revoicechat.core.repository.ServerRepository;
import fr.revoicechat.core.technicaldata.server.NewServer;
import fr.revoicechat.security.UserHolder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * Service responsible for managing {@link Server} entities.
 * <p>
 * This service acts as a bridge between the application layer and the persistence layer,
 * providing methods to:
 * <ul>
 *     <li>Retrieve a list of all servers (from an external provider)</li>
 *     <li>Retrieve a single server by its ID</li>
 *     <li>Create new servers</li>
 *     <li>Update existing servers</li>
 * </ul>
 * <p>
 */
@ApplicationScoped
public class ServerService {

  private final ServerEntityService serverEntityService;
  private final ServerRepository serverRepository;
  private final NewServerCreator newServerCreator;
  private final UserHolder userHolder;
  private final EntityManager entityManager;

  public ServerService(final ServerEntityService serverEntityService,
                       final ServerRepository serverRepository,
                       final NewServerCreator newServerCreator,
                       final UserHolder userHolder,
                       final EntityManager entityManager) {
    this.serverEntityService = serverEntityService;
    this.serverRepository = serverRepository;
    this.newServerCreator = newServerCreator;
    this.userHolder = userHolder;
    this.entityManager = entityManager;
  }

  /** @return a list of available servers, possibly empty */
  public List<Server> getAll() {
    return serverRepository.findAll();
  }

  /**
   * Creates and stores a new server in the database.
   *
   * @param newServer the server entity to persist
   * @return the persisted server entity with its generated ID
   */
  @Transactional
  public Server create(final NewServer newServer) {
    Server server = new Server();
    server.setName(newServer.name());
    server.setType(Optional.ofNullable(newServer.serverType()).orElse(ServerType.PUBLIC));
    newServerCreator.create(server);
    ServerUser serverUser = new ServerUser();
    serverUser.setServer(server);
    serverUser.setUser(userHolder.get());
    entityManager.persist(serverUser);
    return server;
  }

  /**
   * Updates an existing server in the database.
   * <p>
   * The ID of the provided entity will be overridden with the given {@code id}
   * before persisting.
   *
   * @param id             the ID of the server to update
   * @param newServer the updated server data
   * @return the updated and persisted server entity
   */
  @Transactional
  public Server update(final UUID id, final NewServer newServer) {
    var server = serverEntityService.getEntity(id);
    server.setName(newServer.name());
    entityManager.persist(server);
    return server;
  }
}
