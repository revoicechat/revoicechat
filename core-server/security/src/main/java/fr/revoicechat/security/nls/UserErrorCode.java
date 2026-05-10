package fr.revoicechat.security.nls;

import fr.revoicechat.i18n.ErrorLocalizedMessage;

public enum UserErrorCode implements ErrorLocalizedMessage {
  USER_NOT_FOUND,
  USER_PASSWORD_INVALID,
  USER_PASSWORD_WRONG_CONFIRMATION,
  AUTH_CONFIG_ERROR,
  TOTP_NOT_VALID,
}
