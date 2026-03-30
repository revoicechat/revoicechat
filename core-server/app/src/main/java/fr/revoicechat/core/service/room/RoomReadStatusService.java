package fr.revoicechat.core.service.room;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.model.room.Room;
import fr.revoicechat.core.model.room.RoomReadStatus;
import fr.revoicechat.core.model.room.RoomUserId;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.repository.RoomRepository;
import fr.revoicechat.core.technicaldata.message.UnreadMessageStatus;
import fr.revoicechat.security.UserHolder;
import fr.revoicechat.web.error.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class RoomReadStatusService {

  private final EntityManager entityManager;
  private final UserHolder userHolder;
  private final RoomRepository roomRepository;
  private final RoomAccessVerifier roomAccessVerifier;

  public RoomReadStatusService(final EntityManager entityManager, UserHolder userHolder,
                               RoomRepository roomRepository,
                               RoomAccessVerifier roomAccessVerifier) {
    this.entityManager = entityManager;
    this.userHolder = userHolder;
    this.roomRepository = roomRepository;
    this.roomAccessVerifier = roomAccessVerifier;
  }

  public UnreadMessageStatus getUnreadMessagesStatus(final Server server) {
    return roomRepository.findRoomsByUserServers(userHolder.getId())
                         .filter(room -> Objects.equals(server, room.getServer()))
                         .filter(roomAccessVerifier::verify)
                         .map(this::getUnreadMessagesStatus)
                         .reduce(UnreadMessageStatus.none(), UnreadMessageStatus::merge);
  }

  public UnreadMessageStatus getUnreadMessagesStatus(final Room room) {
    return room.isVoiceRoom() ? UnreadMessageStatus.none()
                              : roomRepository.findUnreadSummary(room, userHolder.get())
                                              .toUnreadMessageStatus();
  }

  @Transactional
  public void update(UUID roomId, UUID lastMessageId) {
    var roomReadStatus = get(roomId);
    if (roomReadStatus == null) {
      roomReadStatus = new RoomReadStatus();
      roomReadStatus.setUser(userHolder.get());
      roomReadStatus.setRoom(getRoom(roomId));
    }
    roomReadStatus.setLastMessageId(lastMessageId);
    roomReadStatus.setLastReadAt(OffsetDateTime.now());
    entityManager.persist(roomReadStatus);
  }

  public RoomReadStatus get(UUID roomId) {
    RoomUserId roomUserId = new RoomUserId();
    roomUserId.setRoom(roomId);
    roomUserId.setUser(userHolder.getId());
    return entityManager.find(RoomReadStatus.class, roomUserId);
  }

  public ServerRoom getRoom(final UUID roomId) {
    return Optional.ofNullable(entityManager.find(ServerRoom.class, roomId))
                   .orElseThrow(() -> new ResourceNotFoundException(ServerRoom.class, roomId));
  }
}
