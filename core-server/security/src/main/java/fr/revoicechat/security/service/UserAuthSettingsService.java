package fr.revoicechat.security.service;

import fr.revoicechat.security.UserHolder;
import fr.revoicechat.security.representation.AuthSettingRepresentation;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserAuthSettingsService {

  private final UserHolder userHolder;
  private final RecoverCodesService recoverCodesService;

  public UserAuthSettingsService(UserHolder userHolder, RecoverCodesService recoverCodesService) {
    this.userHolder = userHolder;
    this.recoverCodesService = recoverCodesService;
  }

  public AuthSettingRepresentation ofCurrentUser() {
    var user = userHolder.currentUser();
    return new AuthSettingRepresentation(
        user.getTotpStatus().active(),
        recoverCodesService.retrieveHowManyLeft(user.getId())
    );
  }
}
