package com.georent.exception;

public class OrderOverlapsApprovedOrdersException extends RuntimeException {

    public OrderOverlapsApprovedOrdersException() {
    }

    public OrderOverlapsApprovedOrdersException(String message) {
        super(message);
    }

    public OrderOverlapsApprovedOrdersException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderOverlapsApprovedOrdersException(Throwable cause) {
        super(cause);
    }

    public OrderOverlapsApprovedOrdersException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
