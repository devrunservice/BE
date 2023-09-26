package com.devrun.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;


public class AWSS3Service {

    @Autowired
    protected S3Client s3Client;
    
    @Autowired
    protected S3Presigner s3Presigner;

    @Value("${cloud.aws.bucket}")
    protected String bucketName;


}
