package fr.revoicechat.core.nls;

import fr.revoicechat.i18n.ErrorLocalizedMessage;

public enum UserErrorCode implements ErrorLocalizedMessage {
  USER_NOT_FOUND,
  USER_LOGIN_INVALID,
  USER_WITH_NO_VALID_INVITATION,
  USER_PASSWORD_WRONG,
  USER_PASSWORD_WRONG_CONFIRMATION,
}
