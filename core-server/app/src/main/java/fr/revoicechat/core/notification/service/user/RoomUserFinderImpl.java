package fr.revoicechat.core.notification.service.user;

import static fr.revoicechat.core.risk.RoomRiskType.SERVER_ROOM_READ;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.repository.RoomRepository;
import fr.revoicechat.core.repository.UserRepository;
import fr.revoicechat.live.voice.service.VoiceRoomUserFinder;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.risk.service.RiskService;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoomUserFinderImpl implements RoomUserFinder, VoiceRoomUserFinder {

  private final RiskService riskService;
  private final RoomRepository roomRepository;
  private final UserRepository userRepository;

  public RoomUserFinderImpl(RiskService riskService, RoomRepository roomRepository, UserRepository userRepository) {
    this.riskService = riskService;
    this.roomRepository = roomRepository;
    this.userRepository = userRepository;
  }

  @Override
  public Stream<NotificationRegistrable> find(UUID room) {
    var risk = new RiskEntity(roomRepository.getServerId(room), room);
    return userRepository.findByRoom(room)
                         .filter(user -> riskService.hasRisk(user.getId(), risk, SERVER_ROOM_READ))
                         .map(NotificationRegistrable.class::cast);
  }
}
