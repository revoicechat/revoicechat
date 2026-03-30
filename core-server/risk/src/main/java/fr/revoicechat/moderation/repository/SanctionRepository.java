package fr.revoicechat.moderation.repository;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.moderation.model.Sanction;

public interface SanctionRepository {

  Stream<Sanction> getSanctions(final UUID userId);

  Stream<Sanction> getSanctions(final UUID userId, UUID serverId);

  Stream<Sanction> findAll();
}
