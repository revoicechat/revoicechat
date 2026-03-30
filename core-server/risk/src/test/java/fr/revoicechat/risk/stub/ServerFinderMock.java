package fr.revoicechat.risk.stub;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.risk.service.server.ServerFinder;
import fr.revoicechat.risk.service.user.UserServerFinder;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServerFinderMock implements ServerFinder, UserServerFinder {
  @Override
  public void existsOrThrow(final UUID id) {
    // nothing here
  }

  @Override
  public Stream<NotificationRegistrable> findUserForServer(final UUID serverId) {
    return Stream.of();
  }
}
