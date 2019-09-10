package com.georent.exception;

public class ForgotException extends RuntimeException {
    /**
     * Constructs a <code>MultiPartFileValidationException</code>.
     */
    public ForgotException() {
    }

    /**
     * Constructs a <code>MultiPartFileValidationException</code> with the specified message.
     *
     * @param message the detail message.
     */
    public ForgotException(final String message) {
        super(message);
    }
}
