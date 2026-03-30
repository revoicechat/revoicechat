package fr.revoicechat.core.repository.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.model.User;
import fr.revoicechat.core.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public User findByLogin(String login) {
    List<User> users = entityManager.createQuery("""
                                        select u
                                        from User u
                                        where u.login = :login""", User.class)
                                    .setParameter("login", login)
                                    .getResultList();
    if (users.isEmpty()) {
      return null;
    }
    return users.getFirst();
  }

  @Override
  public Stream<User> findByServers(UUID serverID) {
    return entityManager
        .createQuery("""
            select su.user
            from ServerUser su
            where su.server.id = :serverID""", User.class)
        .setParameter("serverID", serverID)
        .getResultStream();
  }

  @Override
  public long count() {
    return entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
  }

  @Override
  public Stream<User> everyone() {
    return entityManager.createQuery("SELECT u FROM User u", User.class).getResultStream();
  }

  @Override
  public Stream<User> findByRoom(final UUID room) {
    return entityManager
        .createQuery("""
            select su.user
            from ServerUser su
            join ServerRoom room on su.server = room.server
            where room.id = :room
            union
            select u
            from PrivateMessageRoom pm
            join pm.users u
            where pm.id = :room""", User.class)
        .setParameter("room", room)
        .getResultStream();
  }
}
