package fr.revoicechat.core.service.room;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.core.model.room.PrivateMessageMode;
import fr.revoicechat.core.model.room.PrivateMessageRoom;
import fr.revoicechat.core.repository.PrivateMessageRoomRepository;
import fr.revoicechat.core.service.user.UserService;
import fr.revoicechat.security.UserHolder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PrivateMessageEntityService {

  private final EntityManager entityManager;
  private final UserHolder userHolder;
  private final UserService userService;
  private final PrivateMessageRoomRepository privateMessageRoomRepository;

  public PrivateMessageEntityService(EntityManager entityManager,
                                     UserHolder userHolder,
                                     UserService userService,
                                     PrivateMessageRoomRepository privateMessageRoomRepository) {
    this.entityManager = entityManager;
    this.userHolder = userHolder;
    this.userService = userService;
    this.privateMessageRoomRepository = privateMessageRoomRepository;
  }

  @Transactional
  public PrivateMessageRoom getOrCreate(final UUID userId) {
    var room = getDirectDiscussion(userId, userHolder.getId());
    if (room == null) {
      room = new PrivateMessageRoom();
      room.setId(UUID.randomUUID());
      room.setMode(PrivateMessageMode.DIRECT_MESSAGE);
      room.setUsers(List.of(userHolder.get(), userService.getUser(userId)));
      entityManager.persist(room);
    }
    return room;
  }

  private PrivateMessageRoom getDirectDiscussion(UUID user1, UUID user2) {
    return privateMessageRoomRepository.getDirectDiscussion(user1, user2);
  }
}
