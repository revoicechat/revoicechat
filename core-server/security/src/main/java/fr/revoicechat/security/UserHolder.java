package fr.revoicechat.security;

import java.util.UUID;

import fr.revoicechat.security.model.AuthenticatedUser;

public interface UserHolder {
  <T extends AuthenticatedUser> T get();
  UUID getId();
  AuthenticatedUser get(String jwtToken);
  UUID peekId(String jwtToken);

  default <T extends AuthenticatedUser> T getOrNull() {
    try {
      return get();
    } catch (Exception _) {
      return null;
    }
  }
}
