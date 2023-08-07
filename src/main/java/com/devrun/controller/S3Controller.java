package com.devrun.controller;

import com.devrun.dto.S3PathEnum;
import com.devrun.service.AwsS3ReadService;
import com.devrun.service.AwsS3UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@RestController
@Api(tags = "S3Upload Controller", description = "API endpoints for uploading to S3 Storage")
public class S3Controller {

    @Autowired
    private AwsS3UploadService awsS3UploadService;

    @Autowired
    private AwsS3ReadService awsS3ReadService;

    //파일을 지정된 버킷의 루트 디렉토리에 업로드합니다.

    @PostMapping("/{path}/upload")
    @ApiOperation("첨부된 파일을 S3 폴더에 저장합니다.")
    public ResponseEntity profileIMGload( @PathVariable String path , @RequestParam(name = "file") List<MultipartFile> files) {
        try{
            S3PathEnum.valueOf(path);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The 'path' parameter not allowed.");
        }

        try {
            awsS3UploadService.putS3(files , path);
            return ResponseEntity.ok("저장 성공");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading files to S3.");

        } catch (StringIndexOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No files attached. Please attach at least one file.");

        }
    }

    //S3에 저장된 파일명을 입력하면, 파일이 저장된 URL을 반환합니다.
    @ResponseBody
    @GetMapping("/read")
    public String fileRead(@RequestParam(name = "no") Long no){

        String key = "profile/da834627-a0d5-4348-9934-4dfeac3264e0.png";
        String result = awsS3ReadService.findUploadKeyUrl(key);
        return result;
    }

    //public.lecture.{lecture_no}.
    //public.profile.{userid
    //public.
}