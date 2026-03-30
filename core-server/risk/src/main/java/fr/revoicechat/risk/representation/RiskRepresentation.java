package fr.revoicechat.risk.representation;

import java.util.UUID;

import fr.revoicechat.risk.model.RiskMode;
import fr.revoicechat.risk.type.RiskType;

public record RiskRepresentation(
    RiskType type,
    UUID entity,
    RiskMode mode
) {}
