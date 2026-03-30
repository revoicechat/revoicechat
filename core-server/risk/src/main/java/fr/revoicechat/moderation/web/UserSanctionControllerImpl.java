package fr.revoicechat.moderation.web;

import java.util.List;
import java.util.UUID;

import fr.revoicechat.moderation.representation.SanctionRepresentation;
import fr.revoicechat.moderation.service.SanctionService;
import fr.revoicechat.moderation.web.api.UserSanctionController;
import fr.revoicechat.security.utils.RevoiceChatRoles;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed(RevoiceChatRoles.ROLE_USER)
public class UserSanctionControllerImpl implements UserSanctionController {

  private final SanctionService sanctionService;

  public UserSanctionControllerImpl(SanctionService sanctionService) {
    this.sanctionService = sanctionService;
  }

  @Override
  public List<SanctionRepresentation> getAppSanctions(final UUID userId) {
    return Mapper.mapAll(sanctionService.getSanctions(userId));
  }

  @Override
  public List<SanctionRepresentation> getAppSanctions(final UUID userId, final UUID serverId) {
    return Mapper.mapAll(sanctionService.getSanctions(userId, serverId));
  }
}
