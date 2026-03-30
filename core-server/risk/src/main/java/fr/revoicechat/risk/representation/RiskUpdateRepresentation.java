package fr.revoicechat.risk.representation;

import java.util.UUID;

import fr.revoicechat.risk.model.RiskMode;

public record RiskUpdateRepresentation(RiskMode mode, UUID entity) {}
