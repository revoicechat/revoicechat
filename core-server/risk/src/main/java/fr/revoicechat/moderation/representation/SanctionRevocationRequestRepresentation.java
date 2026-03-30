package fr.revoicechat.moderation.representation;

import java.time.LocalDateTime;
import java.util.UUID;

import fr.revoicechat.moderation.model.RequestStatus;

public record SanctionRevocationRequestRepresentation(
    UUID id,
    UUID sanctionId,
    String message,
    RequestStatus status,
    LocalDateTime requestAt,
    boolean canRequestAgain) {}
