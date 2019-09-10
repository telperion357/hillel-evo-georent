package com.georent.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenericResponse<T> {
    private String method;
    private String path;
    private String cause;
    private int statusCode;
    private T body;
}
