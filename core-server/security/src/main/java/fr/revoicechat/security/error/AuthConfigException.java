package fr.revoicechat.security.error;

/** Exception thrown when an issue occurs during the authentication configuration */
public class AuthConfigException extends RuntimeException {

    public AuthConfigException(Throwable cause) {
        super(cause);
    }
}