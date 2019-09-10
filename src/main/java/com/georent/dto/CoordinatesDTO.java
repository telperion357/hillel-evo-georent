package com.georent.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CoordinatesDTO {

    @NotBlank
    @Size(max = 150)
    private String address;
    private Float latitude;
    private Float longitude;
}