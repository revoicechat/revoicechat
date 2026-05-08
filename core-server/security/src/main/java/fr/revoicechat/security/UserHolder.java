package fr.revoicechat.security;

import java.util.UUID;

import fr.revoicechat.security.model.AuthenticatedUser;

public interface UserHolder {

  UUID getId();

  AuthenticatedUser currentUser();

  AuthenticatedUser get(String jwtToken);

  UUID peekId(String jwtToken);
}
