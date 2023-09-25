package com.devrun.youtube;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devrun.service.AwsS3UploadService;

@RestController
public class LectureregistController {

    private final LectureService lectureService;
    private final AwsS3UploadService awsS3UploadService;
    private final YouTubeUploader youTubeUploader;
    private final LecutureCategoryService categoryService;


    @Autowired
    public LectureregistController(LectureService lectureService, AwsS3UploadService awsS3UploadService ,YouTubeUploader youTubeUploader,LecutureCategoryService categoryService) {
    	this.categoryService = categoryService;
        this.lectureService = lectureService;
        this.awsS3UploadService = awsS3UploadService;
        this.youTubeUploader = youTubeUploader;
        
    }
    
    // GET 요청을 통해 카테고리 목록을 가져오는 엔드포인트
    @GetMapping("/lectureregist/categories")
    public ResponseEntity<List<LectureCategory>> getAllCategories() {
        List<LectureCategory> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // GET 요청을 통해 마지막 섹션 ID를 가져오는 엔드포인트
    @GetMapping("/lectureregist/lastsectionid")
    public ResponseEntity<Long> getLastSectionId() {
        Long lastSectionId = lectureService.getLastSectionId();
        return ResponseEntity.ok(lastSectionId);
    }
    
    // POST 요청을 통해 강의 등록을 처리하는 엔드포인트
    @PostMapping("/lectureregist")
    public ResponseEntity<String> createLecture(
            @ModelAttribute CreateLectureRequestDto requestDto,
            @RequestParam("image") List<MultipartFile> imageFiles,
            @RequestParam("videoFileList") List<MultipartFile> videoFiles,
            @RequestParam("categoryNo") Long categoryNo,
            @RequestParam("lectureBigCategory") String lectureBigCategory,
            @RequestParam("lectureMidCategory") String lectureMidCategory,
            @RequestParam("accessToken") String accessToken
            ) {
        try {
            if (videoFiles.isEmpty()) {
                return ResponseEntity.badRequest().body("동영상 파일을 선택해주세요.");
            }
            

            LecturecategoryDto categoryDto = new LecturecategoryDto();
            categoryDto.setLectureBigCategory(lectureBigCategory);
            categoryDto.setLectureMidCategory(lectureMidCategory);
            categoryDto.setCategoryNo(categoryNo);
            
            // 이미지 업로드 후 URL 가져오기
            String imageUrls = awsS3UploadService.putS3(imageFiles, "public.lecture.images" , requestDto.getLectureName());

            
            // 강의 및 비디오 정보를 데이터베이스에 저장하고, 강의 썸네일 이미지를 S3에 업로드한 URL을 가져옴
            Lecture savedLecture = lectureService.saveLecture(requestDto, imageUrls, categoryDto);

            System.out.println("Number of video files to upload: " + videoFiles.size());

            // 동영상 업로드 및 정보 저장
            List<VideoInfo> videoInfoList = new ArrayList<>();
            for (MultipartFile videoFile : videoFiles) {
                VideoInfo videoInfo = youTubeUploader.uploadVideo(videoFile, savedLecture.getId(), accessToken); // savedLecture의 ID와 엑세스 토큰 사용
                System.out.println("아이디???? " + savedLecture.getId());
                videoInfoList.add(videoInfo);
            }
            
            
            
            // 동영상 정보를 데이터베이스에 저장
            lectureService.saveVideoInfo(videoInfoList, savedLecture);

            // 동영상 업로드를 시작하기 위해 업로드 페이지로 리다이렉션
            return ResponseEntity.ok("강의 및 비디오 정보가 저장되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }

}