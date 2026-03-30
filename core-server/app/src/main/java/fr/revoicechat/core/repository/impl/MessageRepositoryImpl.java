package fr.revoicechat.core.repository.impl;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.repository.MessageRepository;
import fr.revoicechat.core.repository.impl.message.MessageSearcher;
import fr.revoicechat.core.repository.page.PageResult;
import fr.revoicechat.core.technicaldata.message.MessageFilterParams;
import fr.revoicechat.security.UserHolder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class MessageRepositoryImpl implements MessageRepository {

  private final EntityManager entityManager;
  private final UserHolder userHolder;
  private final MessageSearcher messageSearcher;

  public MessageRepositoryImpl(EntityManager entityManager, UserHolder userHolder, MessageSearcher messageSearcher) {
    this.entityManager = entityManager;
    this.userHolder = userHolder;
    this.messageSearcher = messageSearcher;
  }

  @Override
  public PageResult<Message> search(MessageFilterParams params) {
    return messageSearcher.search(userHolder.getId(), params);
  }

  @Override
  public Stream<Message> findByRoom(ServerRoom room) {
    return entityManager.createQuery("""
                 SELECT m
                 FROM Message m
                 WHERE m.room = :room
                 ORDER BY m.createdDate DESC""", Message.class)
                        .setParameter("room", room)
                        .getResultStream();
  }

  @Override
  public Message findByMedia(final UUID mediaId) {
    return entityManager.createQuery("""
                 select m
                 from Message m
                 join m.mediaDatas md
                 where md.id = :id""", Message.class)
                        .setParameter("id", mediaId)
                        .getSingleResult();
  }

}
