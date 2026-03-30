package fr.revoicechat.core.notification.service;

import fr.revoicechat.core.model.MediaData;
import fr.revoicechat.notification.data.NotificationActionType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MediaDataNotifierService {

  private final Instance<MediaNotifier> mediaNotifiers;

  @Inject
  public MediaDataNotifierService(Instance<MediaNotifier> mediaNotifiers) {
    this.mediaNotifiers = mediaNotifiers;
  }

  @Transactional
  public void notify(MediaData mediaData, NotificationActionType actionType) {
    mediaNotifiers.stream()
                  .filter(notifier -> notifier.origin().equals(mediaData.getOrigin()))
                  .findFirst()
                  .ifPresent(notifier -> notifier.notify(mediaData, actionType));
  }
}
