package fr.revoicechat.core.nls;

import fr.revoicechat.i18n.ErrorLocalizedMessage;

public enum MessageErrorCode implements ErrorLocalizedMessage {
  MESSAGE_CANNOT_BE_EMPTY,
  MESSAGE_TOO_LONG,
  MEDIA_DATA_SHOULD_HAVE_A_NAME,
  MESSAGE_ANSWERED_DOES_NOT_EXIST,
  ANSWER_MUST_BE_IN_THE_SAME_ROOM
}
