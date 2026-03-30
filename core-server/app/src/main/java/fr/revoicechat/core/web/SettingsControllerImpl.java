package fr.revoicechat.core.web;

import static fr.revoicechat.security.utils.RevoiceChatRoles.ROLE_USER;

import java.util.Map;
import java.util.UUID;

import fr.revoicechat.core.service.settings.SettingsService;
import fr.revoicechat.core.service.user.UserSettingsService;
import fr.revoicechat.core.web.api.SettingsController;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

public class SettingsControllerImpl implements SettingsController {

  private final UserSettingsService userSettingsService;
  private final SettingsService settingsService;

  public SettingsControllerImpl(UserSettingsService userSettingsService, SettingsService settingsService) {
    this.userSettingsService = userSettingsService;
    this.settingsService = settingsService;
  }

  @Override
  @PermitAll
  public Map<String, Object> genealSetings() {
    return settingsService.get();
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public String me() {
    return userSettingsService.ofCurrentUser();
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public String ofUser(final UUID id) {
    return userSettingsService.ofUser(id);
  }

  @Override
  @RolesAllowed(ROLE_USER)
  public String me(final String settings) {
    return userSettingsService.updateForCurrentUser(settings);
  }
}
