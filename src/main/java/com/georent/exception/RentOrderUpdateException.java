package com.georent.exception;

public class RentOrderUpdateException extends RuntimeException {
    public RentOrderUpdateException() {
    }

    public RentOrderUpdateException(String message) {
        super(message);
    }

    public RentOrderUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public RentOrderUpdateException(Throwable cause) {
        super(cause);
    }
}
