package com.georent.exception;

public class InitializationException extends RuntimeException {
    public InitializationException() {
    }

    public InitializationException(final String message) {
        super(message);
    }

    public InitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
