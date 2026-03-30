package fr.revoicechat.core.notification.service.profil;

import java.util.UUID;
import java.util.stream.Stream;

import fr.revoicechat.core.notification.ProfilPictureUpdate;
import fr.revoicechat.web.error.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProfilPictureEmitter {

  private final ServerPictureUpdater serverPictureUpdater;
  private final UserPictureUpdater userPictureUpdater;

  public ProfilPictureEmitter(ServerPictureUpdater serverPictureUpdater, UserPictureUpdater userPictureUpdater) {
    this.serverPictureUpdater = serverPictureUpdater;
    this.userPictureUpdater = userPictureUpdater;
  }

  public void emmit(final UUID id) {
    Stream.of(serverPictureUpdater, userPictureUpdater)
          .filter(pictureUpdater -> pictureUpdater.isPresent(id))
          .findFirst()
          .orElseThrow(() -> new ResourceNotFoundException(ProfilPictureUpdate.class, id))
          .emmit(id);
  }
}
