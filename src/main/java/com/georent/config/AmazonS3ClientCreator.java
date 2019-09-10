package com.georent.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3ClientCreator {

    private final GeoRentS3Credentials geoRentS3Credentials;

    @Autowired
    public AmazonS3ClientCreator(GeoRentS3Credentials geoRentS3Credentials) {
        this.geoRentS3Credentials = geoRentS3Credentials;
    }

    @Bean
    public AmazonS3 getS3client() {
        String awsAccessKeyId = geoRentS3Credentials.getAWSAccessKeyId();
        String awsSecretKey = geoRentS3Credentials.getAWSSecretKey();

        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretKey);

//        ClientConfiguration clientConfig = new ClientConfiguration();
//        clientConfig.setProtocol(Protocol.HTTP);
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withClientConfiguration(clientConfig)
                .withRegion(Regions.EU_WEST_1)
                .build();
        return s3client;
    }

}
