package fr.revoicechat.moderation.representation;

import java.time.LocalDateTime;
import java.util.UUID;

import fr.revoicechat.moderation.model.SanctionType;

public record NewSanction(UUID targetedUser,
                          SanctionType type,
                          String reason,
                          LocalDateTime expiresAt) {}
