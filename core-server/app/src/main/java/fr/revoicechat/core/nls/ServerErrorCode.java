package fr.revoicechat.core.nls;

import fr.revoicechat.i18n.ErrorLocalizedMessage;

public enum ServerErrorCode implements ErrorLocalizedMessage {
  SERVER_STRUCTURE_WITH_ROOM_THAT_DOES_NOT_EXISTS,
  SERVER_NOT_PUBLIC,
  NO_VALID_INVITATION,
  ;

  @Override
  public String toString() {
    return translate();
  }
}
