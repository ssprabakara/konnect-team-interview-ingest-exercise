package konnect.exception;

public class ConfigNotFoundException extends RuntimeException {

    public ConfigNotFoundException(final String message, final Throwable cause,
                                   final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ConfigNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConfigNotFoundException(final String message) {
        super(message);
    }

    public ConfigNotFoundException(final Throwable cause) {
        super(cause);
    }
}
