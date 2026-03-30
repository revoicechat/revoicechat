package fr.revoicechat.core.notification.service;

import fr.revoicechat.core.model.MediaData;
import fr.revoicechat.core.model.MediaOrigin;
import fr.revoicechat.notification.data.NotificationActionType;

public interface MediaNotifier {

  void notify(MediaData mediaData, NotificationActionType actionType);

  MediaOrigin origin();
}
