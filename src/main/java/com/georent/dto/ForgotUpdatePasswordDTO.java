package com.georent.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ForgotUpdatePasswordDTO {
    @NotBlank()
    @Pattern(regexp="[A-Za-z0-9_-]+", message="Invalid password format")
    @Size(min = 8, max = 64)
    private String password;
}
