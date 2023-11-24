package com.devrun.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.S3PathEnum;
import com.devrun.service.AwsS3ReadService;
import com.devrun.service.AwsS3UploadService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@RestController
@Api(tags = "S3업로드 API")
public class S3Controller {

	@Autowired
	private AwsS3UploadService awsS3UploadService;

	@Autowired
	private AwsS3ReadService awsS3ReadService;

	// S3에 저장된 파일명을 입력하면, 파일이 저장된 URL을 반환합니다.
	@GetMapping("/read")
	@ApiOperation("S3 서버에서 파일을 검색합니다.")
	@ApiImplicitParam(name = "filename", value = "파일 전체 경로" , example = "/profile/0f04abaa-1cfa-44d9-95cc-0bcfea45206eabc123" , dataTypeClass = String.class)
	public ResponseEntity<?> fileRead(@RequestParam(name = "filename") String filename) {
		String fileURL = awsS3ReadService.findUploadKeyUrl(filename);
		return ResponseEntity.ok(fileURL);
	}
	
	@PostMapping(value = "/{path}/presignurl")
	@ApiOperation("파일을 업로드할 pre-signUrl을 반환합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "path", value = "첨부파일 경로" , example = "profile" , dataTypeClass = String.class),
		@ApiImplicitParam(name = "fileinfo", value = "첨부할 파일의 정보입니다. 키값은 fileName , fileExt 로 각각 첨부 파일의 파일명과, 파일의 확장자를 의미합니다. path : profile,lectuer_thumbnail,notice,lecture_note,lecture_qa,lecture_comment" , dataTypeClass = Object.class)
		})
	@ApiResponse(message = "파일을 업로드할 url을 담은 JSON 객체입니다. 키값은 presignUrl 입니다.", code = 200 , response = JSONObject.class)
	public ResponseEntity<?> getPresignUrlForUploadImage(@PathVariable String path , @RequestBody Map<String , String> fileinfo) throws IOException {
		try {
			S3PathEnum.valueOf(path);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The 'path' parameter not allowed.");
		}
		String url;
		try {
			url = awsS3UploadService.getPresignUrl(path , fileinfo);
			Map<String , String> respone = new HashMap<String, String>();
			respone.put("presignUrl", url);
			JSONObject responejson = new JSONObject(respone);
			return ResponseEntity.ok(responejson);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return ResponseEntity.badRequest().body("This Extension is not allow");
		}
	}
}