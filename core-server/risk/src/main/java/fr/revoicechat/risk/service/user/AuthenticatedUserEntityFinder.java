package fr.revoicechat.risk.service.user;

import java.util.Optional;
import java.util.UUID;

import fr.revoicechat.security.model.AuthenticatedUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class AuthenticatedUserEntityFinder {
  private final EntityManager entityManager;

  public AuthenticatedUserEntityFinder(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public AuthenticatedUser getUser(final UUID id) {
    return Optional.ofNullable(entityManager.find(AuthenticatedUser.class, id))
        .orElseThrow(() -> new NotFoundException("User not found"));
  }
}
