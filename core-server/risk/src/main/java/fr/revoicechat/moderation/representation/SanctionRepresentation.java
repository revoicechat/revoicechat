package fr.revoicechat.moderation.representation;

import java.time.LocalDateTime;
import java.util.UUID;

import fr.revoicechat.moderation.model.SanctionType;

public record SanctionRepresentation(UUID id,
                                     UserRepresentation targetedUser,
                                     UUID server,
                                     SanctionType type,
                                     LocalDateTime startAt,
                                     LocalDateTime expiresAt,
                                     UserRepresentation issuedBy,
                                     String reason,
                                     UserRepresentation revokedBy,
                                     LocalDateTime revokedAt,
                                     boolean active) {}
