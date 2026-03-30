package fr.revoicechat.core.repository.impl;

import java.util.List;
import java.util.stream.Stream;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.ServerUser;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.repository.ServerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class ServerRepositoryImpl implements ServerRepository {
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<Server> findAll() {
    return entityManager.createQuery("SELECT s FROM Server s", Server.class).getResultList();
  }

  @Override
  public Stream<Server> getByUser(final User user) {
    return entityManager
        .createQuery("""
            SELECT su.server
            FROM ServerUser su
            WHERE su.user = :user""", Server.class)
        .setParameter("user", user)
        .getResultStream();
  }

  @Override
  public Stream<ServerUser> getServerUser(final Server server) {
    return entityManager.createQuery("""
                                  SELECT su
                                  FROM ServerUser su
                                  WHERE su.server = :server""", ServerUser.class)
                        .setParameter("server", server)
                        .getResultStream();
  }



  @Override
  public Stream<Server> getPublicServer() {
    return entityManager
        .createQuery("SELECT s FROM Server s", Server.class)
        .getResultStream()
        .filter(Server::isPublic);
  }
}
