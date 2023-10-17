package com.devrun.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class AwsS3DeleteService extends AWSS3Service {
	
	
	public void DeleteObject(String keyname) {		

        try {
            DeleteObjectRequest multiObjectDeleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(keyname)
                .build();

            s3Client.deleteObject(multiObjectDeleteRequest);
            System.out.println("Multiple objects are deleted!");
        
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
	}
}
