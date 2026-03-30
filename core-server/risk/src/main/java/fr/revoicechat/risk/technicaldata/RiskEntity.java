package fr.revoicechat.risk.technicaldata;

import java.util.UUID;

public record RiskEntity(UUID serverId, UUID entityId) {
  public static final RiskEntity EMPTY = new RiskEntity(null, null);
}
