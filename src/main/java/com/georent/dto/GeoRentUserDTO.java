package com.georent.dto;


import com.georent.domain.UserRole;
import lombok.*;

@Data
public class GeoRentUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
//    private String role;
}