package fr.revoicechat.risk.repository;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.risk.model.Risk;

public interface RiskRepository {

  Stream<Risk> getRisks(final UUID server);
}
