package konnect.exception;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class KafkaClientCustomException extends Exception {
    private static final String EXCEPTION_PATTERN = "%s (statusCode=%d, statusMessage=%s)";
    private final int statusCode;
    private final String statusMessage;

    public KafkaClientCustomException(final String message,
                                      final int statusCode,
                                      final String statusMessage) {
        super(String.format(EXCEPTION_PATTERN, message, statusCode, statusMessage));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(message), "message shouldn't be null or empty");
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }
}
