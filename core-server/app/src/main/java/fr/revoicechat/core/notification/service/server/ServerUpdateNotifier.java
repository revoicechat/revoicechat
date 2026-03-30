package fr.revoicechat.core.notification.service.server;

import static fr.revoicechat.core.notification.service.NotificationUserRetriever.findUserForServer;
import static fr.revoicechat.notification.data.NotificationActionType.MODIFY;

import fr.revoicechat.core.model.Server;
import fr.revoicechat.core.notification.ServerUpdateNotification;
import fr.revoicechat.notification.Notification;
import fr.revoicechat.web.mapper.Mapper;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class ServerUpdateNotifier {

  public void update(Server server) {
    notifyUpdate(new ServerUpdateNotification(Mapper.mapLight(server), MODIFY));
  }

  private void notifyUpdate(final ServerUpdateNotification notification) {
    Notification.of(notification).sendTo(findUserForServer(notification.server().id()));
  }
}
