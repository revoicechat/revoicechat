package fr.revoicechat.live.stub;

import java.util.Set;
import java.util.UUID;

import fr.revoicechat.risk.service.user.AuthenticatedUserEntityFinder;
import fr.revoicechat.security.model.AuthenticatedUser;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthenticatedUserEntityFinderMock implements AuthenticatedUserEntityFinder {

  @Override
  @SuppressWarnings("unchecked")
  public <T extends AuthenticatedUser> T getUser(final UUID id) {
    return (T) new AuthenticatedUserMock(id);
  }

  record AuthenticatedUserMock(UUID getId) implements AuthenticatedUser {
    @Override public String getDisplayName() {return "";}
    @Override public String getLogin() {return "";}
    @Override public Set<String> getRoles() {return Set.of();}
  }
}
