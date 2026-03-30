package fr.revoicechat.core.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import java.util.UUID;

import fr.revoicechat.core.notification.service.profil.ProfilPictureEmitter;
import fr.revoicechat.core.web.api.ProfilPictureController;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed(ROLE_USER)
public class ProfilPictureControllerImpl implements ProfilPictureController {

  private final ProfilPictureEmitter profilPictureEmitter;

  public ProfilPictureControllerImpl(final ProfilPictureEmitter profilPictureEmitter) {
    this.profilPictureEmitter = profilPictureEmitter;
  }

  @Override
  public void updateProfilPicture(final UUID id) {
    profilPictureEmitter.emmit(id);
  }
}
