package com.devrun.service;

import com.devrun.entity.MemberEntity;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3UploadService extends AWSS3Service{

    @Autowired
    private MemberService memberService;


    //파일 업로드를 위한 요청서를 만듭니다.
    private PutObjectRequest getPutObjectRequest(String key , String contentType){

        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();
    }


    //작성된 요청서와 S3Client 계정 정보를 통해 S3 버킷에 접근하고 지정된 파일을 업로드합니다.
    public String putS3(List<MultipartFile> multipartFiles , String uploadpath) throws IOException , StringIndexOutOfBoundsException , NullPointerException{

        for (MultipartFile multipartFile : multipartFiles) {
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

            String uniqueFileName = generateUniqueFileName(originalFilename); // Generate a unique filename using UUID.
            uploadpath += "/" + uniqueFileName;
            PutObjectRequest uploadRequest = getPutObjectRequest(uploadpath, contentType);

            s3Client.putObject(uploadRequest, uploadfile);
        }
        return uploadpath;
    }

    private String generateUniqueFileName(String originalFilename) throws StringIndexOutOfBoundsException{
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        return UUID.randomUUID().toString() + extension;
    }

}
