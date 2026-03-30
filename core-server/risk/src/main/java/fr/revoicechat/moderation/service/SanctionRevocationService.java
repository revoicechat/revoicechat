package fr.revoicechat.moderation.service;

import java.util.List;
import java.util.UUID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import fr.revoicechat.moderation.model.SanctionRevocationRequest;
import fr.revoicechat.moderation.repository.SanctionRevocationRequestRepository;

@ApplicationScoped
public class SanctionRevocationService {

  private final SanctionRevocationRequestRepository repository;

  public SanctionRevocationService(SanctionRevocationRequestRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public List<SanctionRevocationRequest> fetch(UUID serverId) {
    return repository.getByServer(serverId).toList();
  }
}
