package com.georent.exception;

public class LotNotFoundException extends RuntimeException {

    /**
     * Constructs a <code>LotNotFoundException</code>.
     */
    public LotNotFoundException() {
    }

    /**
     * Constructs a <code>LotNotFoundException</code> with the specified message.
     *
     * @param message the detail message.
     */
    public LotNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructs a <code>LotNotFoundException</code> with the specified message.
     *
     * @param message the detail message.
     * @param userId the userId.
     */
    public LotNotFoundException(final String message, Long userId) {
        super(message + Long.toString(userId));
    }

    /**
     * Constructs a {@code UsernameNotFoundException} with the specified message and root
     * cause.
     *
     * @param message the detail message.
     * @param cause root cause
     */
    public LotNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
