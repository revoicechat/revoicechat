package fr.revoicechat.security.error;

import static fr.revoicechat.security.nls.UserErrorCode.AUTH_CONFIG_ERROR;

import fr.revoicechat.i18n.LocalizedMessage;
import fr.revoicechat.web.error.BadRequestException;

/** Exception thrown when an issue occurs during the authentication configuration */
public class AuthConfigException extends BadRequestException {

    public AuthConfigException(Throwable cause) {
        this(AUTH_CONFIG_ERROR, cause.getMessage());
    }

    public AuthConfigException(LocalizedMessage message, Object... args) {
        super(message, args);
    }

}