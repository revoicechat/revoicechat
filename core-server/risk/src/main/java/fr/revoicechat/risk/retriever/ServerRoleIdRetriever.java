package fr.revoicechat.risk.retriever;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import fr.revoicechat.risk.RisksEntityRetriever;
import fr.revoicechat.risk.model.ServerRoles;
import fr.revoicechat.risk.technicaldata.RiskEntity;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.EntityManager;

public class ServerRoleIdRetriever implements RisksEntityRetriever {

  @Override
  public RiskEntity get(final Method method, final List<DataParameter> parameters) {
    var em = CDI.current().select(EntityManager.class).get();
    return parameters.stream()
                     .map(DataParameter::arg)
                     .filter(UUID.class::isInstance)
                     .map(UUID.class::cast)
                     .findFirst()
                     .map(id -> em.getReference(ServerRoles.class, id))
                     .map(server -> new RiskEntity(server.getServer(), null))
                     .orElse(RiskEntity.EMPTY);
  }
}
