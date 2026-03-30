package fr.revoicechat.moderation.mapper;

import java.util.Optional;
import java.util.UUID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import fr.revoicechat.moderation.model.Sanction;
import fr.revoicechat.moderation.representation.SanctionRepresentation;
import fr.revoicechat.moderation.representation.UserRepresentation;
import fr.revoicechat.risk.service.user.AuthenticatedUserEntityFinder;
import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.web.mapper.RepresentationMapper;
import io.quarkus.arc.Unremovable;

@Unremovable
@ApplicationScoped
public class SanctionMapper implements RepresentationMapper<Sanction, SanctionRepresentation> {

  private final AuthenticatedUserEntityFinder userEntityFinder;

  public SanctionMapper(final AuthenticatedUserEntityFinder userEntityFinder) {
    this.userEntityFinder = userEntityFinder;
  }

  @Override
  @Transactional
  public SanctionRepresentation map(final Sanction sanction) {
    return new SanctionRepresentation(
        sanction.getId(),
        mapUser(sanction.getTargetedUser()),
        sanction.getServer(),
        sanction.getType(),
        sanction.getStartAt(),
        sanction.getExpiresAt(),
        mapUser(sanction.getIssuedBy()),
        sanction.getReason(),
        mapUser(sanction.getRevokedBy()),
        sanction.getRevokedAt(),
        sanction.isActive()
    );
  }

  private UserRepresentation mapUser(final UUID userId) {
    return Optional.ofNullable(userId)
                   .<AuthenticatedUser>map(userEntityFinder::getUser)
                   .map(this::mapUser)
                   .orElse(null);
  }

  private UserRepresentation mapUser(final AuthenticatedUser user) {
    return new UserRepresentation(user.getId(), user.getDisplayName());
  }
}
