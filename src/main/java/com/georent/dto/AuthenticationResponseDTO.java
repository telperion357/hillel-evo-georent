package com.georent.dto;

import com.georent.domain.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class AuthenticationResponseDTO {
    private String accessToken;
    private String tokenType;
    private LocalDate dateCreate;
    private Long expiresIn;
//    private Set<UserRole> role;
    private Set<String> role;
}
