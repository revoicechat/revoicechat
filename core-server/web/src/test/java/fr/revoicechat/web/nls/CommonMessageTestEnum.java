package fr.revoicechat.web.nls;

import fr.revoicechat.i18n.LocalizedMessage;

public enum CommonMessageTestEnum implements LocalizedMessage {
  TEST_ENUM;

  @Override
  public String fileName() {
    return getClass().getCanonicalName();
  }
}
