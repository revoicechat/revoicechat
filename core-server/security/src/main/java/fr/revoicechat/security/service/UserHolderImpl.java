package fr.revoicechat.security.service;

import static fr.revoicechat.security.nls.UserErrorCode.USER_NOT_FOUND;

import java.util.UUID;

import fr.revoicechat.security.UserHolder;
import fr.revoicechat.security.model.AuthenticatedUser;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class UserHolderImpl implements UserHolder {

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
  public UUID getId() {
    return UUID.fromString(securityIdentity.getPrincipal().getName());
  }

  @Override
  public AuthenticatedUser currentUser() {
    return getUser(getId());
  }

  @Override
  public AuthenticatedUser get(final String jwtToken) {
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

  private AuthenticatedUser getUser(UUID id) {
    var user = entityManager.find(AuthenticatedUser.class, id);
    if (user == null) {
      throw new NotFoundException(USER_NOT_FOUND.translate());
    }
    return user;
  }
}
