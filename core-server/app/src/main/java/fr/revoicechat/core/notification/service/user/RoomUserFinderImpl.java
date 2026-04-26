package fr.revoicechat.core.notification.service.user;

import static fr.revoicechat.core.risk.RoomRiskType.SERVER_ROOM_READ;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.model.room.PrivateMessageRoom;
import fr.revoicechat.core.model.room.Room;
import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.core.repository.RoomRepository;
import fr.revoicechat.core.repository.UserRepository;
import fr.revoicechat.live.voice.service.VoiceRoomUserFinder;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.risk.service.RiskService;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class RoomUserFinderImpl implements RoomUserFinder, VoiceRoomUserFinder {

  private final RiskService riskService;
  private final RoomRepository roomRepository;
  private final UserRepository userRepository;
  private final EntityManager entityManager;

  public RoomUserFinderImpl(RiskService riskService, RoomRepository roomRepository, UserRepository userRepository, EntityManager entityManager) {
    this.riskService = riskService;
    this.roomRepository = roomRepository;
    this.userRepository = userRepository;
    this.entityManager = entityManager;
  }

  @Override
  public Stream<NotificationRegistrable> find(UUID room) {
    Room r = entityManager.find(Room.class, room);
    if (r == null) {
      throw new IllegalArgumentException("Invalid room id " + room);
    }
    return find(r);
  }

  @Override
  public Stream<NotificationRegistrable> find(final Room room) {
    if (room instanceof PrivateMessageRoom privateMessageRoom) {
      return findByPrivateMessageRoom(privateMessageRoom);
    } else if (room instanceof ServerRoom serverRoom) {
      return findByServerRoom(serverRoom.getId());
    }
    return Stream.empty();
  }

  private Stream<NotificationRegistrable> findByPrivateMessageRoom(final PrivateMessageRoom room) {
    return room.getUsers().stream().map(NotificationRegistrable.class::cast);
  }

  private Stream<NotificationRegistrable> findByServerRoom(final UUID room) {
    var risk = new RiskEntity(roomRepository.getServerId(room), room);
    return userRepository.findByRoom(room)
                         .filter(user -> riskService.hasRisk(user.getId(), risk, SERVER_ROOM_READ))
                         .map(NotificationRegistrable.class::cast);
  }
}
