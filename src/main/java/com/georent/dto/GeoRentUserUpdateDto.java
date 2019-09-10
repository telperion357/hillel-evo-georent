package com.georent.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class GeoRentUserUpdateDto {
    private String firstName;
    private String lastName;
    @Size(min = 12, max = 12)
    @Pattern(regexp="[0-9-]+", message="Invalid phoneNumber format.")
    private String phoneNumber;
}