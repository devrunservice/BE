package com.devrun.service;


import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import java.net.URL;

@Service
public class AwsS3ReadService extends AWSS3Service {

    //파일의 url을 반환합니다.
    public String findUploadKeyUrl (String key) {
        S3Utilities s3Utilities = s3Client.utilities();
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        URL url = s3Utilities.getUrl(request);

        return url.toString();
    }

    private GetObjectRequest createGetRequest(String key){
        GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .key(key)
                .bucket(bucketName)
                .build();

        return objectRequest;
    }


//    public String getnoticename(Long no) {
//        String key = noticerepo.findById(no).get().getPid();
//        return key;
//    }
}
