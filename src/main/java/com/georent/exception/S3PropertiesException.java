package com.georent.exception;

import com.amazonaws.AbortedException;

public class S3PropertiesException extends AbortedException {

    public S3PropertiesException(String message) {
        super(message);
    }
}
