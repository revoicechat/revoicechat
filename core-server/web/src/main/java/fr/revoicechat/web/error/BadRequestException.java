package fr.revoicechat.web.error;

import fr.revoicechat.i18n.LocalizedMessage;

/**
 * Exception thrown when a requested resource cannot be found.
 * This exception will automatically result in an HTTP 404 Not Found response.
 */
public class BadRequestException extends RuntimeException {

  public BadRequestException(LocalizedMessage message, Object... args) {
    super(message.translate(args));
  }
}
