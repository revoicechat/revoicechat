package fr.revoicechat.core.security;

import static fr.revoicechat.core.nls.UserErrorCode.USER_NOT_FOUND;

import java.util.UUID;

import fr.revoicechat.core.model.User;
import fr.revoicechat.notification.NotificationRegistrableHolder;
import fr.revoicechat.security.UserHolder;
import fr.revoicechat.security.service.SecurityTokenService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class UserHolderImpl implements UserHolder, NotificationRegistrableHolder {

  private final SecurityTokenService securityTokenService;
  private final SecurityIdentity securityIdentity;
  private final EntityManager entityManager;

  public UserHolderImpl(SecurityTokenService securityTokenService,
                        SecurityIdentity securityIdentity,
                        EntityManager entityManager) {
    this.securityTokenService = securityTokenService;
    this.securityIdentity = securityIdentity;
    this.entityManager = entityManager;
  }

  @Override
  @SuppressWarnings("unchecked")
  public User get() {
    var id = UUID.fromString(securityIdentity.getPrincipal().getName());
    return getUser(id);
  }

  @Override
  public UUID getId() {
    return get().getId();
  }

  @Override
  public User get(final String jwtToken) {
    try {
      return getUser(peekId(jwtToken));
    } catch (NotFoundException _) {
      return null;
    } catch (Exception _) {
      throw new WebApplicationException("Invalid token", 401);
    }
  }

  @Override
  public UUID peekId(final String jwtToken) {
    return securityTokenService.retrieveUserAsId(jwtToken);
  }

  private User getUser(UUID id) {
    var user = entityManager.find(User.class, id);
    if (user == null) {
      throw new NotFoundException(USER_NOT_FOUND.translate());
    }
    return user;
  }
}
