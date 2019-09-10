package com.georent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.header")
@Data
public class HeaderProperties {

    private String type;
    private String name;
}
