package fr.revoicechat.security.model;

import java.util.Set;
import java.util.UUID;

public interface AuthenticatedUser {
  UUID getId();
  String getDisplayName();
  String getLogin();
  Set<String> getRoles();
}