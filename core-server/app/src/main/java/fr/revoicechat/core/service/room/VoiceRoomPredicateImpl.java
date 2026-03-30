package fr.revoicechat.core.service.room;

import java.util.UUID;

import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.live.voice.service.VoiceRoomPredicate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class VoiceRoomPredicateImpl implements VoiceRoomPredicate {
  private final EntityManager entityManager;

  public VoiceRoomPredicateImpl(final EntityManager entityManager) {this.entityManager = entityManager;}

  @Override
  public boolean isVoiceRoom(final UUID roomId) {
    var room = entityManager.find(ServerRoom.class, roomId);
    return room != null && room.isVoiceRoom();
  }
}
