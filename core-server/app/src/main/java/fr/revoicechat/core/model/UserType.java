package fr.revoicechat.core.model;

import static fr.revoicechat.security.utils.RevoiceChatRoles.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum UserType {
  ADMIN(ROLE_ADMIN),
  USER,
  BOT,
  ;

  private final Set<String> roles;

  UserType(String... roles) {
    var set = new HashSet<>(List.of(ROLE_USER));
    set.addAll(Arrays.asList(roles));
    this.roles = Collections.unmodifiableSet(set);
  }

  public Set<String> getRoles() {
    return roles;
  }
}
