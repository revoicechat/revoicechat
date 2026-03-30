package fr.revoicechat.core.technicaldata.user;

import fr.revoicechat.notification.model.ActiveStatus;

public record UpdatableUserData(
    String displayName,
    PasswordUpdated password,
    ActiveStatus status
) {
  public record PasswordUpdated(String password,
                                String newPassword,
                                String confirmPassword) {}
}
