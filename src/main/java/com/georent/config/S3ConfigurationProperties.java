package com.georent.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aws")
public class S3ConfigurationProperties {

    private String secretKey;
    private String accessKey;
    private String bucketName;
    private String andPointUrl;
    private Long expiresIn;
    private Long fileSizeMax;

}
