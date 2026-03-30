package fr.revoicechat.core.service.room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.room.PrivateMessageRoom;
import fr.revoicechat.core.repository.PrivateMessageRoomRepository;
import fr.revoicechat.core.service.message.MessageService;
import fr.revoicechat.core.technicaldata.message.NewMessage;
import fr.revoicechat.security.UserHolder;
import fr.revoicechat.web.error.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PrivateMessageService {

  private final EntityManager entityManager;
  private final UserHolder userHolder;
  private final PrivateMessageEntityService privateMessageEntityService;
  private final PrivateMessageRoomRepository privateMessageRoomRepository;
  private final MessageService messageService;

  public PrivateMessageService(final EntityManager entityManager, final UserHolder userHolder, final PrivateMessageEntityService privateMessageEntityService, final PrivateMessageRoomRepository privateMessageRoomRepository, final MessageService messageService) {
    this.entityManager = entityManager;
    this.userHolder = userHolder;
    this.privateMessageEntityService = privateMessageEntityService;
    this.privateMessageRoomRepository = privateMessageRoomRepository;
    this.messageService = messageService;
  }

  public List<PrivateMessageRoom> findAll() {
    return privateMessageRoomRepository.findByUserId(userHolder.getId()).toList();
  }

  public PrivateMessageRoom get(final UUID roomId) {
    return Optional.ofNullable(entityManager.find(PrivateMessageRoom.class, roomId))
                   .orElseThrow(() -> new ResourceNotFoundException(PrivateMessageService.class, roomId));
  }

  public PrivateMessageRoom getDirectDiscussion(final UUID userId) {
    return Optional.ofNullable(getDirectDiscussion(userId, userHolder.getId()))
                   .orElseThrow(() -> new ResourceNotFoundException(PrivateMessageService.class, userId));
  }

  @Transactional
  public Message sendPrivateMessageTo(final UUID userId, final NewMessage newMessage) {
    var room = privateMessageEntityService.getOrCreate(userId);
    return messageService.create(room.getId(), newMessage);
  }

  private PrivateMessageRoom getDirectDiscussion(UUID user1, UUID user2) {
    return privateMessageRoomRepository.getDirectDiscussion(user1, user2);
  }
}
