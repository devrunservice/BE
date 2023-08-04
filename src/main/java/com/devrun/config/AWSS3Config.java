package com.devrun.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSS3Config implements WebMvcConfigurer {

    private final String accessKey;
    private final String secretKey;
    private final String region;

    public AWSS3Config(@Value("${cloud.aws.credentials.access-key}") String accessKey,
                       @Value("${cloud.aws.credentials.secret-key}") String secretKey ,
                       @Value("${cloud.aws.region.static}") String region){
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
    }

    @Bean
    public AwsCredentials basciAwsCredentials() {
        return AwsBasicCredentials.create(accessKey , secretKey);
    }

    @Bean
    public S3Client s3Client(AwsCredentials awsCredentials) {
        return S3Client.builder().region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

}
