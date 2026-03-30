package fr.revoicechat.core.retriever;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fr.revoicechat.core.model.room.ServerRoom;
import fr.revoicechat.live.common.service.RoomRisksEntityRetriever;
import fr.revoicechat.risk.RisksEntityRetriever;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class EntityByRoomIdRetriever implements RisksEntityRetriever, RoomRisksEntityRetriever {

  @Override
  public RiskEntity get(final Method method, final List<DataParameter> parameters) {
    return parameters.stream()
                     .map(DataParameter::arg)
                     .filter(UUID.class::isInstance)
                     .map(UUID.class::cast)
                     .map(this::get)
                     .findFirst()
                     .orElse(RiskEntity.EMPTY);
  }

  @Override
  public RiskEntity get(final UUID roomId) {
    return Optional.ofNullable(roomId)
                   .map(id -> getEntityManager().getReference(ServerRoom.class, id))
                   .map(room -> new RiskEntity(room.getServer().getId(), room.getId()))
                   .orElse(RiskEntity.EMPTY);
  }

  private static EntityManager getEntityManager() {
    return CDI.current().select(EntityManager.class).get();
  }
}
