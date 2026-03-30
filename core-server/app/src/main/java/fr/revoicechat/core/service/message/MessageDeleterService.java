package fr.revoicechat.core.service.message;

import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.repository.MessageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class MessageDeleterService {

  private final EntityManager entityManager;
  private final MessageRepository messageRepository;

  public MessageDeleterService(EntityManager entityManager, MessageRepository messageRepository) {
    this.entityManager = entityManager;
    this.messageRepository = messageRepository;
  }

  public void deleteAllFrom(final ServerRoom room) {
    messageRepository.findByRoom(room).forEach(entityManager::remove);
  }
}
