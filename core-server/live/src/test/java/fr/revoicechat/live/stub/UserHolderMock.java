package fr.revoicechat.live.stub;

import java.util.Set;
import java.util.UUID;

import fr.revoicechat.notification.NotificationRegistrableHolder;
import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.security.UserHolder;
import fr.revoicechat.security.model.AuthenticatedUser;
import fr.revoicechat.security.service.SecurityTokenService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserHolderMock implements UserHolder, NotificationRegistrableHolder {

  private final SecurityIdentity securityIdentity;
  private final SecurityTokenService tokenService;

  public UserHolderMock(final SecurityIdentity securityIdentity, final SecurityTokenService tokenService) {
    this.securityIdentity = securityIdentity;
    this.tokenService = tokenService;
  }

  @Override
  @SuppressWarnings("unchecked")
  public AuthenticatedUserMock get() {
    return new AuthenticatedUserMock(UUID.fromString(securityIdentity.getPrincipal().getName()));
  }

  @Override
  public UUID getId() {
    return UUID.fromString(securityIdentity.getPrincipal().getName());
  }

  @Override
  public AuthenticatedUser get(final String jwtToken) {
    return new AuthenticatedUserMock(peekId(jwtToken));
  }

  @Override
  public UUID peekId(final String jwtToken) {
    return tokenService.retrieveUserAsId(jwtToken);
  }

  public record AuthenticatedUserMock(UUID getId) implements AuthenticatedUser, NotificationRegistrable {

    @Override
    public String getDisplayName() {
      return "";
    }

    @Override
    public String getLogin() {
      return "";
    }

    @Override
    public Set<String> getRoles() {
      return Set.of();
    }

    @Override
    public ActiveStatus getStatus() {
      return ActiveStatus.ONLINE;
    }
  }
}