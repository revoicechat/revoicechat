package fr.revoicechat.core.service.room;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import fr.revoicechat.core.model.Message;
import fr.revoicechat.core.model.User;
import fr.revoicechat.core.model.room.PrivateMessageMode;
import fr.revoicechat.core.model.room.PrivateMessageRoom;
import fr.revoicechat.core.repository.PrivateMessageRoomRepository;
import fr.revoicechat.core.representation.NewPrivateMessageRoom;
import fr.revoicechat.core.service.message.MessageService;
import fr.revoicechat.core.service.user.UserService;
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
  private final UserService userService;

  public PrivateMessageService(final EntityManager entityManager, final UserHolder userHolder, final PrivateMessageEntityService privateMessageEntityService, final PrivateMessageRoomRepository privateMessageRoomRepository, final MessageService messageService, final UserService userService) {
    this.entityManager = entityManager;
    this.userHolder = userHolder;
    this.privateMessageEntityService = privateMessageEntityService;
    this.privateMessageRoomRepository = privateMessageRoomRepository;
    this.messageService = messageService;
    this.userService = userService;
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

  @Transactional
  public PrivateMessageRoom create(final NewPrivateMessageRoom newRoom) {
    var room = new PrivateMessageRoom();
    room.setId(UUID.randomUUID());
    room.setMode(PrivateMessageMode.DIRECT_MESSAGE);
    room.setName(newRoom.name());
    Set<User> users = new HashSet<>();
    users.add(userHolder.get());
    newRoom.users().stream().map(userService::getUser).forEach(users::add);
    room.setUsers(new ArrayList<>(users));
    entityManager.persist(room);
    return room;
  }
}
