package fr.revoicechat.risk.retriever;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import fr.revoicechat.risk.RisksEntityRetriever;
import fr.revoicechat.risk.technicaldata.RiskEntity;

public class ServerIdRetriever implements RisksEntityRetriever {

  @Override
  public RiskEntity get(final Method method, final List<DataParameter> parameters) {
    return parameters.stream()
                     .map(DataParameter::arg)
                     .filter(UUID.class::isInstance)
                     .map(UUID.class::cast)
                     .findFirst()
                     .map(server -> new RiskEntity(server, null))
                     .orElse(RiskEntity.EMPTY);
  }
}
