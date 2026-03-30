package fr.revoicechat.risk.service.user;

import java.util.UUID;

import fr.revoicechat.security.model.AuthenticatedUser;

public interface AuthenticatedUserEntityFinder {

  <T extends AuthenticatedUser> T getUser(final UUID id);
}
