package com.georent.config;

import com.amazonaws.auth.AWSCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeoRentS3Credentials implements AWSCredentials {

    private final S3ConfigurationProperties properties;

    @Autowired
    public GeoRentS3Credentials(S3ConfigurationProperties properties) {
        this.properties = properties;
    }

    public String getAWSAccessKeyId(){
        return properties.getAccessKey();
    }

    public String getAWSSecretKey(){
        return properties.getSecretKey();
    }
}
