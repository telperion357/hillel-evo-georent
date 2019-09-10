package com.georent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class MultiPartFileValidationException extends RuntimeException {


    /**
     * Constructs a <code>MultiPartFileValidationException</code>.
     */
    public MultiPartFileValidationException() {
    }

    /**
     * Constructs a <code>MultiPartFileValidationException</code> with the specified message.
     *
     * @param message the detail message.
     */
    public MultiPartFileValidationException(final String message) {
        super(message);
    }
}
