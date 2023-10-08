package com.devrun.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class AwsS3UploadService extends AWSS3Service {	

	private String generateUniqueFileName(String originalFilename) throws StringIndexOutOfBoundsException {
		return UUID.randomUUID().toString() + originalFilename;
	}
	
	 private PutObjectRequest getPutObjectRequest(String key , String contentType){

	        return PutObjectRequest.builder()
	                .bucket(bucketName)
	                .key(key)
	                .contentType(contentType)
	                .build();
	    }

	public String getPresignUrl(String path, Map<String, String> fileinfo) throws Exception{
			String fileName = fileinfo.get("fileName");
			String fileExt = fileinfo.get("fileExt");
			if(!fileExt.equals("jpg") && !fileExt.equals("png") && !fileExt.equals("webp") && !fileExt.equals("jpeg")) {
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

	public String putS3(MultipartFile multipartFile , String uploadpath , String uniqueName) throws IOException , StringIndexOutOfBoundsException , NullPointerException{


            String originalFilename = multipartFile.getOriginalFilename();
            String fieldName = multipartFile.getName();
            String contentType = multipartFile.getContentType();
            boolean empty = multipartFile.isEmpty();
            long fileSize = multipartFile.getSize();

            System.out.println(
                    originalFilename + "\n" +
                            fieldName + "\n" +
                            contentType + "\n" +
                            empty + "\n" +
                            fileSize + "\n"
            );

            InputStream file = multipartFile.getInputStream();

            long contentlength = multipartFile.getSize();
            RequestBody uploadfile = RequestBody.fromInputStream(file, contentlength);
            uploadpath += "/" + uniqueName;
            PutObjectRequest uploadRequest = getPutObjectRequest(uploadpath, contentType);
            s3Client.putObject(uploadRequest, uploadfile);
            uploadpath = "https://devrun-dev-bucket.s3.ap-northeast-2.amazonaws.com/"+ uploadpath;


        
        
        return uploadpath;
    }
}
