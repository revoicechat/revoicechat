package fr.revoicechat.core.service.server;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.repository.UserRepository;
import fr.revoicechat.notification.model.NotificationRegistrable;
import fr.revoicechat.risk.service.user.UserServerFinder;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserServerFinderImpl implements UserServerFinder {

  private final UserRepository userRepository;

  public UserServerFinderImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Stream<NotificationRegistrable> findUserForServer(final UUID serverId) {
    return userRepository.findByServers(serverId).map(NotificationRegistrable.class::cast);
  }
}
