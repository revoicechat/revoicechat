package fr.revoicechat.risk.representation;

import java.util.List;
import java.util.UUID;

public record ServerRoleRepresentation(
    UUID id,
    String name,
    String color,
    int priority,
    UUID serverId,
    List<RiskRepresentation> risks,
    List<UUID> members
) {
}
