package com.georent.exception;

public class RegistrationSuchUserExistsException extends RuntimeException {

    /**
     * Constructs a <code>RegistrationSuchUserExistsException</code>.
     */
    public RegistrationSuchUserExistsException() {
    }

    /**
     * Constructs a <code>RegistrationSuchUserExistsException</code> with the specified message.
     *
     * @param message the detail message.
     */
    public RegistrationSuchUserExistsException(final String message) {
        super(message);
    }

    /**
     * Constructs a {@code RegistrationSuchUserExistsException} with the specified message and root
     * cause.
     *
     * @param message the detail message.
     * @param cause root cause
     */
    public RegistrationSuchUserExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
