package fr.revoicechat.moderation.repository;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.moderation.model.SanctionRevocationRequest;
import fr.revoicechat.moderation.model.Sanction;

public interface SanctionRevocationRequestRepository {
  Stream<SanctionRevocationRequest> getBySanction(Sanction sanction);
  Stream<SanctionRevocationRequest> getByUser(UUID userId);
  Stream<SanctionRevocationRequest> getByServer(UUID serverId);
}
