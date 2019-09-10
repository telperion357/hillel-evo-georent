package com.georent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfigurationProperties {

    @NotBlank
    private String secretKey;

    @NotBlank
    private Long expiresIn;
}

