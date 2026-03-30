package fr.revoicechat.moderation.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import fr.revoicechat.moderation.model.SanctionRevocationRequest;
import fr.revoicechat.moderation.representation.SanctionRevocationRequestRepresentation;
import fr.revoicechat.web.mapper.RepresentationMapper;
import io.quarkus.arc.Unremovable;

@Unremovable
@ApplicationScoped
public class SanctionRevocationRequestMapper implements RepresentationMapper<SanctionRevocationRequest, SanctionRevocationRequestRepresentation> {

  @Override
  @Transactional
  public SanctionRevocationRequestRepresentation map(final SanctionRevocationRequest request) {
    return new SanctionRevocationRequestRepresentation(
        request.getId(),
        request.getSanction().getId(),
        request.getMessage(),
        request.getStatus(),
        request.getRequestAt(),
        request.canRequestAgain()
    );
  }
}
