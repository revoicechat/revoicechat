package fr.revoicechat.core.service.room;

import static java.time.Clock.systemDefaultZone;

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
import fr.revoicechat.core.service.user.UserRetriever;
import fr.revoicechat.core.technicaldata.message.UnreadMessageStatus;
import fr.revoicechat.web.error.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class RoomReadStatusService {

  private final EntityManager entityManager;
  private final UserRetriever userRetriever;
  private final RoomRepository roomRepository;
  private final RoomAccessVerifier roomAccessVerifier;

  public RoomReadStatusService(EntityManager entityManager,
                               UserRetriever userRetriever,
                               RoomRepository roomRepository,
                               RoomAccessVerifier roomAccessVerifier) {
    this.entityManager = entityManager;
    this.userRetriever = userRetriever;
    this.roomRepository = roomRepository;
    this.roomAccessVerifier = roomAccessVerifier;
  }

  public UnreadMessageStatus getUnreadMessagesStatus(final Server server) {
    return roomRepository.findRoomsByUserServers(userRetriever.currentUserId())
                         .filter(room -> Objects.equals(server, room.getServer()))
                         .filter(roomAccessVerifier::verify)
                         .map(this::getUnreadMessagesStatus)
                         .reduce(UnreadMessageStatus.none(), UnreadMessageStatus::merge);
  }

  public UnreadMessageStatus getUnreadMessagesStatus(final Room room) {
    return room.isVoiceRoom() ? UnreadMessageStatus.none()
                              : roomRepository.findUnreadSummary(room, userRetriever.currentUser())
                                              .toUnreadMessageStatus();
  }

  @Transactional
  public void update(UUID roomId, UUID lastMessageId) {
    var roomReadStatus = get(roomId);
    if (roomReadStatus == null) {
      roomReadStatus = new RoomReadStatus();
      roomReadStatus.setUser(userRetriever.currentUser());
      roomReadStatus.setRoom(getRoom(roomId));
    }
    roomReadStatus.setLastMessageId(lastMessageId);
    roomReadStatus.setLastReadAt(OffsetDateTime.now(systemDefaultZone()));
    entityManager.persist(roomReadStatus);
  }

  public RoomReadStatus get(UUID roomId) {
    RoomUserId roomUserId = new RoomUserId();
    roomUserId.setRoom(roomId);
    roomUserId.setUser(userRetriever.currentUserId());
    return entityManager.find(RoomReadStatus.class, roomUserId);
  }

  public ServerRoom getRoom(final UUID roomId) {
    return Optional.ofNullable(entityManager.find(ServerRoom.class, roomId))
                   .orElseThrow(() -> new ResourceNotFoundException(ServerRoom.class, roomId));
  }
}
