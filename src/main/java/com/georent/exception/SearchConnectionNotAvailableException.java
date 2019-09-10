package com.georent.exception;

import javax.persistence.PersistenceException;

public class SearchConnectionNotAvailableException extends PersistenceException {


    public SearchConnectionNotAvailableException(String msg) {
        super(msg);
    }

    /**
     *
     * @param msg
     * @param cause
     */
    public SearchConnectionNotAvailableException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
