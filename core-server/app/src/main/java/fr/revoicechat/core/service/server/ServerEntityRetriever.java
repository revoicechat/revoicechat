package fr.revoicechat.core.service.server;

import java.util.UUID;

import fr.revoicechat.core.model.Server;

public interface ServerEntityRetriever {

  /**
   * Retrieves a server from the database by its unique identifier.
   *
   * @param id the unique server ID
   * @return the server entity
   * @throws java.util.NoSuchElementException if no server with the given ID exists
   */
  Server getEntity(final UUID id);
}
