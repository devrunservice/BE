package com.devrun.service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class AwsS3UploadService extends AWSS3Service {	

	private String generateUniqueFileName(String originalFilename) throws StringIndexOutOfBoundsException {
		return UUID.randomUUID().toString() + originalFilename;
	}

	public String getPresignUrl(String path, Map<String, String> fileinfo) throws Exception{
			String fileName = generateUniqueFileName(fileinfo.get("fileName"));
			String fileExt = fileinfo.get("fileExt");
			if(!fileExt.equals("jpg") && !fileExt.equals("png")) {
				throw new Exception("This Extension is not allow");
			}
			PutObjectRequest objectRequest = PutObjectRequest.builder()
					.bucket(bucketName)
					.key(path + "/" + fileName)
					.contentType("image/" + fileExt)
					.build();

			PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
					.signatureDuration(Duration.ofMinutes(10))
					.putObjectRequest(objectRequest)
					.build();

			PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
			String myURL = presignedRequest.url().toString();
			System.out.println("Presigned URL to upload a file to: " + myURL);
			System.out.println("Which HTTP method needs to be used when uploading a file: "
					+ presignedRequest.httpRequest().method());			
			return myURL;
	}
}
