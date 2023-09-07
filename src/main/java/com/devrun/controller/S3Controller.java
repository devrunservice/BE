package com.devrun.controller;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devrun.dto.S3PathEnum;
import com.devrun.service.AwsS3ReadService;
import com.devrun.service.AwsS3UploadService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "S3Upload Controller", description = "API endpoints for uploading to S3 Storage")
public class S3Controller {

	@Autowired
	private AwsS3UploadService awsS3UploadService;

	@Autowired
	private AwsS3ReadService awsS3ReadService;

	@PostMapping("/{path}/upload")
	@ApiOperation("첨부된 파일을 S3 폴더에 저장합니다.")
	@ApiImplicitParam(name = "첨부파일", value = "files", dataType = "HTTP multipart/form-data")
	public ResponseEntity<?> TransferToS3(@PathVariable String path,
			@RequestParam(name = "file") List<MultipartFile> files) {
		try {
			S3PathEnum.valueOf(path);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The 'path' parameter not allowed.");
		}

		try {
			String fileURL = awsS3UploadService.putS3(files, path);
			JSONObject respone = new JSONObject();
			respone.put("fileURL", fileURL);
			respone.put("msg", "저장 완료");
			return ResponseEntity.ok(respone);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading files to S3.");

		} catch (StringIndexOutOfBoundsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("No files attached. Please attach at least one file.");

		}
	}

	// S3에 저장된 파일명을 입력하면, 파일이 저장된 URL을 반환합니다.
	@GetMapping("/read")
	public ResponseEntity<?> fileRead(@RequestParam(name = "filename") String filename) {
		String fileURL = awsS3ReadService.findUploadKeyUrl(filename);
		return ResponseEntity.ok(fileURL);
	}
}