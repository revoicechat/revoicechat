package fr.revoicechat.core.representation;

import fr.revoicechat.core.model.Message;

public record OpenGraphSchemaHolder(Message message) {
  public String text() {
    return message.getText();
  }
}
