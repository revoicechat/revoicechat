package fr.revoicechat.core.notification.service.profil;

import java.util.UUID;

sealed interface PictureUpdater<T> permits ServerPictureUpdater, UserPictureUpdater {
  T get(UUID id);

  void emmit(T t);

  default boolean isPresent(UUID id) {
    return get(id) != null;
  }

  default void emmit(UUID id) {
    emmit(get(id));
  }
}