package fr.revoicechat.risk.stub;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import java.util.Set;
import java.util.UUID;

import fr.revoicechat.notification.NotificationRegistrableHolder;
import fr.revoicechat.notification.model.ActiveStatus;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.security.UserHolder;
import fr.revoicechat.security.model.AuthenticatedUser;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserHolderMock implements UserHolder, NotificationRegistrableHolder {

  @Override
  @SuppressWarnings("unchecked")
  public AuthenticatedUserMock get() {
    return new AuthenticatedUserMock(UUID.randomUUID());
  }

  @Override
  public UUID getId() {
    return UUID.randomUUID();
  }

  @Override
  public AuthenticatedUser get(final String jwtToken) {
    return new AuthenticatedUserMock(peekId(jwtToken));
  }

  @Override
  public UUID peekId(final String jwtToken) {
    return UUID.randomUUID();
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
      return Set.of(ROLE_USER);
    }

    @Override
    public ActiveStatus getStatus() {
      return ActiveStatus.ONLINE;
    }
  }
}