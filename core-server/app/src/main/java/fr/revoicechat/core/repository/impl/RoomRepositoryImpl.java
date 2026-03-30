package fr.revoicechat.core.repository.impl;

import static java.util.function.Predicate.not;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.model.User;
import fr.revoicechat.core.model.room.Room;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.repository.RoomRepository;
import fr.revoicechat.core.repository.impl.room.RoomUnreadSummary;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class RoomRepositoryImpl implements RoomRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<ServerRoom> findByServerId(UUID serverId) {
    return entityManager
        .createQuery("SELECT r FROM ServerRoom r where r.server.id = :serverId", ServerRoom.class)
        .setParameter("serverId", serverId)
        .getResultList();
  }

  @Override
  public List<UUID> findIdThatAreNotInRoom(UUID serverId, List<UUID> ids) {
    var idsIn = entityManager.createQuery("""
                                     SELECT r.id
                                     FROM ServerRoom r
                                     WHERE r.id IN :ids
                                     and r.server.id = :serverId
                                 """, UUID.class)
                             .setParameter("ids", ids)
                             .setParameter("serverId", serverId)
                             .getResultList();
    return ids.stream().filter(not(idsIn::contains)).toList();
  }

  @Override
  public UUID getServerId(final UUID room) {
    return entityManager
        .createQuery("SELECT r.server.id FROM ServerRoom r where r.id = :room", UUID.class)
        .setParameter("room", room)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  @Override
  public Stream<ServerRoom> findRoomsByUserServers(final UUID userId) {
    return entityManager.createQuery("""
                                SELECT r
                                FROM ServerRoom r
                                JOIN ServerUser su on su.server = r.server
                                WHERE su.user.id = :id
                            """, ServerRoom.class)
                        .setParameter("id", userId)
                        .getResultStream();
  }

  @Override
  public RoomUnreadSummary findUnreadSummary(final Room room, final User currentUser) {
    Object[] row = (Object[]) entityManager.createQuery("""
                                               SELECT
                                                   (SELECT m2.id FROM Message m2
                                                    LEFT JOIN RoomReadStatus rrs2 ON rrs2.room = :room AND rrs2.user = :currentUser
                                                    WHERE m2.room = :room
                                                      AND m2.user != :currentUser
                                                      AND (rrs2.lastReadAt IS NULL OR m2.createdDate > rrs2.lastReadAt)
                                                    ORDER BY m2.createdDate ASC
                                                    LIMIT 1),
                                                   COUNT(m.id),
                                                   SUM(CASE WHEN answer.user = :currentUser THEN 1 ELSE 0 END),
                                                   SUM(CASE WHEN m.text LIKE CONCAT('%@', :username, '%') THEN 1 ELSE 0 END)
                                               FROM Message m
                                               LEFT JOIN m.answerTo answer
                                               LEFT JOIN RoomReadStatus rrs ON rrs.room = :room AND rrs.user = :currentUser
                                               WHERE m.room = :room
                                                 AND (rrs.lastReadAt IS NULL OR m.createdDate > rrs.lastReadAt)
                                                 AND m.user != :currentUser""")
                                .setParameter("room", room)
                                .setParameter("currentUser", currentUser)
                                .setParameter("username", currentUser.getDisplayName())
                                .getSingleResult();

    return new RoomUnreadSummary(
        (UUID)    row[0],
        (Long)    row[1],
        row[2] != null ? (Long) row[2] : 0L,
        row[3] != null ? (Long) row[3] : 0L
    );  }
}
