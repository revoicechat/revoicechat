package fr.revoicechat.web.error;

import static fr.revoicechat.web.nls.HttpStatusErrorCode.NOT_FOUND;

import java.util.UUID;

/**
 * Exception thrown when a requested resource cannot be found.
 * This exception will automatically result in an HTTP 404 Not Found response.
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(Class<?> clazz, UUID id) {
    super(NOT_FOUND.translate(clazz.getSimpleName(), id));
  }
}
