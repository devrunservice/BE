package com.devrun.youtube;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTubeScopes;

@RestController
public class LectureregistController {

    private final LectureService lectureService;
    private final AwsS3UploadService awsS3UploadService;
    private final YouTubeUploader youTubeUploader;
    private final LecutureCategoryService categoryService;

    HttpTransport httpTransport = new NetHttpTransport();
    JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    GoogleClientSecrets clientSecrets = loadClientSecretsFromFile();  // 파일로부터 클라이언트 비밀 정보 로드하는 예시 메서드
    private static final String redirectUri = "http://localhost:3000/auth/google/callback"; 

    
    
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
    
    private GoogleClientSecrets loadClientSecretsFromFile()  {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("client_secrets.json");
       
        try (Reader reader = new InputStreamReader(inputStream)) {
            return GoogleClientSecrets.load(JSON_FACTORY, reader);
        } catch (IOException e) {
			e.printStackTrace();
		}
		return clientSecrets;
    }

    
    @GetMapping("/auth/google/callback")
    public ResponseEntity<String> handleGoogleCallback(@RequestParam("code") String code) {
        try {
            // Google 인증 콜백에서 전달된 'code' 파라미터를 사용하여 액세스 토큰 교환 등의 작업을 수행합니다.
            
            // OAuth 2.0 인증 코드로부터 액세스 토큰을 교환하기 위한 GoogleAuthorizationCodeFlow 객체를 생성합니다.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(YouTubeScopes.YOUTUBE_UPLOAD))
                    .setAccessType("offline")
                    .build();
            
            // 'code' 값을 사용하여 액세스 토큰을 요청하고, GoogleTokenResponse를 받습니다.
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();
            
            tokenResponse.getAccessToken();

            // 예시: 리다이렉션 URL 생성 및 반환
            String redirectUrl = "https://devrun.site/createVideo";  // 리디렉션할 URL 설정
            return ResponseEntity.ok(redirectUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
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
            
            awsS3UploadService.putS3(imageFiles.get(0), "public.lecture.images" , requestDto.getLectureName());

            
            // 강의 및 비디오 정보를 데이터베이스에 저장하고, 강의 썸네일 이미지를 S3에 업로드한 URL을 가져옴
            //Lecture savedLecture = lectureService.saveLecture(requestDto, imageUrls, categoryDto);

            System.out.println("Number of video files to upload: " + videoFiles.size());

            // 동영상 업로드 및 정보 저장
//            List<VideoInfo> videoInfoList = new ArrayList<>();
//            for (MultipartFile videoFile : videoFiles) {
//                VideoInfo videoInfo = youTubeUploader.uploadVideo(videoFile, savedLecture.getId(), accessToken); // savedLecture의 ID와 엑세스 토큰 사용
//                System.out.println("아이디???? " + savedLecture.getId());
//                videoInfoList.add(videoInfo);
//            }
            
            
            
            // 동영상 정보를 데이터베이스에 저장
            //lectureService.saveVideoInfo(videoInfoList, savedLecture);

            // 동영상 업로드를 시작하기 위해 업로드 페이지로 리다이렉션
            return ResponseEntity.ok("강의 및 비디오 정보가 저장되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }
    
    @PostMapping("/lectureregitest")
    public String lecturetest( @ModelAttribute CreateLectureRequestDto requestDto, @RequestParam("accessToken") String googleAccessToken,    		
    		HttpServletResponse httpServletResponse) throws Exception{
    	System.out.println("--------------------------------lectureregitest Controller --------------------------------");
    	System.out.println(requestDto.getLectureName());
    	System.out.println("accessToken :" + googleAccessToken);
    	System.err.println(requestDto);

    	//VideoDto로 동영상 파일을 유저의 채널에 업로드하고, 비디오 정보를 받아오기
    	//S3에가서 이미지를 업로드하고, 썸네일 URL 받아오기    	
    	//Lecture save
    	//LectureSection save
    	//VideoDto + Lecuture, LectureSection = Video
    	//List<Video> saveAll
    	//List<VideoDto> videolist = requestDto.getVideoList();
//    	VideoDto videolist = requestDto.getVideoList();
    	 List<VideoDto> videolist = requestDto.getVideoList();
    	 System.err.println(videolist);
    	
    	 

    	// 업로드된 비디오 정보를 저장할 리스트를 생성합니다.
    	List<VideoDto> uploadedVideos = new ArrayList<>();

    	requestDto.setVideoList(videolist);
    	
    	try {
    		
    		 // 리스트의 각 비디오에 대해 업로드 작업을 수행합니다.
    	    for (VideoDto video : videolist) {
    	        VideoDto uploadedVideo = youTubeUploader.uploadVideo(video, httpServletResponse);
    	        uploadedVideos.add(uploadedVideo);
    	    }
    		
			String lectureThumnailUrl = awsS3UploadService.putS3(requestDto.getLectureThumbnailFile(), "public.lecture.images" , requestDto.getLectureName());
			
			Lecture savedlecture = lectureService.saveLecture2(requestDto , lectureThumnailUrl);
			
			List<LectureSection> savedlectureSeciton = lectureService.saveLectureSection(savedlecture , requestDto.getLectureSectionList());
			
			lectureService.saveVideo(savedlecture , savedlectureSeciton , videolist);
		} catch (StringIndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	

            return "수신완료"; // Redirect to a success page
        }
    

}