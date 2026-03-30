package fr.revoicechat.moderation.web;

import java.util.List;
import java.util.UUID;
import jakarta.annotation.security.RolesAllowed;

import fr.revoicechat.moderation.model.Sanction;
import fr.revoicechat.moderation.representation.NewSanction;
import fr.revoicechat.moderation.representation.SanctionFilterParams;
import fr.revoicechat.moderation.representation.SanctionRepresentation;
import fr.revoicechat.moderation.representation.SanctionRevocationRequestRepresentation;
import fr.revoicechat.moderation.service.SanctionCreator;
import fr.revoicechat.moderation.service.SanctionEntityService;
import fr.revoicechat.moderation.service.SanctionRevocationService;
import fr.revoicechat.moderation.service.SanctionRevoker;
import fr.revoicechat.moderation.web.api.AppSanctionController;
import fr.revoicechat.security.utils.RevoiceChatRoles;
import fr.revoicechat.web.error.ResourceNotFoundException;
import fr.revoicechat.web.mapper.Mapper;

public class AppSanctionControllerImpl implements AppSanctionController {
  private final SanctionEntityService sanctionEntityService;
  private final SanctionCreator sanctionCreator;
  private final SanctionRevoker sanctionRevoker;
  private final SanctionRevocationService sanctionRevocationService;

  public AppSanctionControllerImpl(SanctionEntityService sanctionEntityService,
                                   SanctionCreator sanctionCreator,
                                   SanctionRevoker sanctionRevoker,
                                   SanctionRevocationService sanctionRevocationService) {
    this.sanctionEntityService = sanctionEntityService;
    this.sanctionCreator = sanctionCreator;
    this.sanctionRevoker = sanctionRevoker;
    this.sanctionRevocationService = sanctionRevocationService;
  }

  @Override
  @RolesAllowed(RevoiceChatRoles.ROLE_USER)
  public List<SanctionRepresentation> getSanctions(final SanctionFilterParams params) {
    return Mapper.mapAll(sanctionEntityService.getAll(null, params));
  }

  @Override
  @RolesAllowed(RevoiceChatRoles.ROLE_USER)
  public SanctionRepresentation getSanction(final UUID id) {
    var sanction = sanctionEntityService.get(id);
    if (sanction.getServer() != null) {
      throw new ResourceNotFoundException(Sanction.class, id);
    }
    return Mapper.map(sanction);
  }

  @Override
  @RolesAllowed(RevoiceChatRoles.ROLE_ADMIN)
  public SanctionRepresentation issueAppLevelSanction(final NewSanction newSanction) {
    return Mapper.map(sanctionCreator.create(null, newSanction));
  }

  @Override
  @RolesAllowed(RevoiceChatRoles.ROLE_ADMIN)
  public void revokeAppLevelSanction(final UUID id) {
    sanctionRevoker.revoke(null, id);
  }

  @Override
  @RolesAllowed(RevoiceChatRoles.ROLE_USER)
  public SanctionRevocationRequestRepresentation askToRevokeSanction(UUID sanctionId, final String message) {
    return Mapper.map(sanctionRevoker.ask(sanctionId, message));
  }

  @Override
  @RolesAllowed(RevoiceChatRoles.ROLE_ADMIN)
  public void rejectRevokeSanctionRequest(final UUID sanctionId) {
    sanctionRevoker.rejectRequest(null, sanctionId);
  }

  @Override
  @RolesAllowed(RevoiceChatRoles.ROLE_USER)
  public List<SanctionRevocationRequestRepresentation> fetchActiveRevocationRequest() {
    return Mapper.mapAll(sanctionRevocationService.fetch(null));

  }
}
