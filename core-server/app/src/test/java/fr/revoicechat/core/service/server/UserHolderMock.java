package fr.revoicechat.core.service.server;

import java.util.UUID;

import fr.revoicechat.security.UserHolder;
import fr.revoicechat.security.model.AuthenticatedUser;

public record UserHolderMock(AuthenticatedUser u) implements UserHolder {

  @Override
  public UUID getId() {
    return u.getId();
  }

  @Override
  public AuthenticatedUser currentUser() {
    return u;
  }

  @Override
  public AuthenticatedUser get(final String jwtToken) {
    return u;
  }

  @Override
  public UUID peekId(final String jwtToken) {
    return u.getId();
  }
}
