package fr.revoicechat.core.repository.impl.room;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.model.room.PrivateMessageRoom;
import fr.revoicechat.core.repository.PrivateMessageRoomRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class PrivateMessageRoomRepositoryImpl implements PrivateMessageRoomRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Stream<PrivateMessageRoom> findByUserId(final UUID userId) {
    return entityManager.createQuery("""
                            select pmr
                            from PrivateMessageRoom pmr
                            join pmr.users u
                            where u.id = :user""", PrivateMessageRoom.class)
                        .setParameter("user", userId)
                        .getResultStream();
  }

  @Override
  public PrivateMessageRoom getDirectDiscussion(final UUID user1, final UUID user2) {
    return entityManager.createQuery("""
                            SELECT r FROM PrivateMessageRoom r
                            JOIN r.users u1
                            JOIN r.users u2
                            WHERE u1.id = :user1
                            AND u2.id = :user2
                            AND SIZE(r.users) = 2""", PrivateMessageRoom.class)
                        .setParameter("user1", user1)
                        .setParameter("user2", user2)
                        .getSingleResultOrNull();
  }
}
