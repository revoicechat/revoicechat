package fr.revoicechat.security.service;

import java.util.Set;
import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.revoicechat.security.model.AuthenticatedUser;

public interface SecurityTokenService {
  String generate(AuthenticatedUser user, Set<String> groups);

  String generateTemporaryToken(AuthenticatedUser user, Set<String> groups);

  UUID retrieveUserAsId(String jwtToken);
  void blackList(final JsonWebToken jsonWebToken);
}
