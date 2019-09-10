package com.georent.dto;

import lombok.Data;

@Data
public class GenericResponseDTO<T> {
    private String message;
    private T body;
}
