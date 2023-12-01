package com.devrun.youtube;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devrun.entity.MemberEntity;
import com.devrun.repository.MemberEntityRepository;
import com.devrun.service.AwsS3UploadService;
import com.devrun.service.MemberService;
import com.devrun.service.MyLectureProgressService;
import com.devrun.util.JWTUtil;
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

	private final DurationExtractor durationExtractor;

	private final LectureService lectureService;
	private final AwsS3UploadService awsS3UploadService;
	private final YouTubeUploader youTubeUploader;
	private final LecutureCategoryService categoryService;
	public static final HttpTransport httpTransport = new NetHttpTransport();
	public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String CREDENTIALS_DIRECTORY = ".oauth-credentials";

	/*
	 * private static final Collection<String> SCOPES = Arrays.asList(
	 * YouTubeScopes.YOUTUBE_UPLOAD, YouTubeScopes.YOUTUBE_READONLY );
	 */

	GoogleClientSecrets clientSecrets = loadClientSecretsFromFile(); // 파일로부터 클라이언트 비밀 정보 로드하는 예시 메서드
	private static final String redirectUri = "http://localhost:3000/auth/google/callback";

	@Autowired
	private MemberEntityRepository memberEntityRepository;

	@Autowired
	public LectureregistController(MemberService memberService, LectureService lectureService,
			AwsS3UploadService awsS3UploadService, YouTubeUploader youTubeUploader,
			LecutureCategoryService categoryService, MyLectureProgressService myLectureProgressService
			) {
		this.durationExtractor = new DurationExtractor();
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

	private GoogleClientSecrets loadClientSecretsFromFile() {
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
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
					clientSecrets, Collections.singleton(YouTubeScopes.YOUTUBE_UPLOAD)).setAccessType("offline")
							.build();

			// 'code' 값을 사용하여 액세스 토큰을 요청하고, GoogleTokenResponse를 받습니다.
			GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();

			tokenResponse.getAccessToken();

			// 예시: 리다이렉션 URL 생성 및 반환
			String redirectUrl = "https://devrun.site/createVideo"; // 리디렉션할 URL 설정
			return ResponseEntity.ok(redirectUrl);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
		}
	}

	@PostMapping("/lectureregitest")
	public String lecturetest(HttpServletRequest httpServletRequest,@Valid @ModelAttribute CreateLectureRequestDto requestDto,
			@RequestParam("oauth2") String googleAccessToken, HttpServletResponse httpServletResponse) throws Exception {
		System.out
				.println("--------------------------------lectureregitest Controller --------------------------------");
		System.out.println(requestDto.getLectureName());
		System.out.println("oauth2 :" + googleAccessToken);
		System.err.println(requestDto);
		// 리스트의 각 비디오에 대해 업로드 작업을 수행합니다.
		List<VideoDto> uploadedVideos = new ArrayList<>(); // 업로드된 비디오 정보를 저장할 리스트를 생성합니다.
		for (VideoDto video : requestDto.getVideoList()) {
			VideoDto uploadedVideo = youTubeUploader.uploadVideo(video, httpServletResponse, googleAccessToken);
			File file = new File("/home/ubuntu/devrun/temp/" + video.getVideofile().getOriginalFilename());
			video.getVideofile().transferTo(file);
			int duration = (int)durationExtractor.extract(file);
			video.setTotalPlayTime(duration);
			file.delete();
			
			uploadedVideos.add(uploadedVideo);
		}
		System.out.println("----------------------------채널 업로드 종료---------------------------------------");
		// JWT 토큰에서 사용자 아이디 추출
		System.out.println("----------------------------JWT 토큰에서 사용자 아이디 추출---------------------------------------");
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		System.out.println("----------------------------멘토(사용자) 정보 조회---------------------------------------");
		// 멘토(사용자) 정보 조회
		MemberEntity mento = memberEntityRepository.findById(userId);
		System.out.println("----------------------------S3 업로드 시작---------------------------------------");
		// 썸네일 S3 저장
		// S3에가서 이미지를 업로드하고, 썸네일 URL 받아오기
		String lectureThumnailUrl = awsS3UploadService.putS3(requestDto.getLectureThumbnail(), "lectuer_thumbnail",
				requestDto.getLectureName());
		System.out.println("----------------------------S3 업로드 종료---------------------------------------");
		System.out.println("----------------------------강의 엔티티 객체 생성 및 매핑---------------------------------------");
		// 강의 엔티티 객체 생성 및 매핑
		Lecture savedlecture = lectureService.saveLecture(mento, requestDto, lectureThumnailUrl);
		System.out.println("----------------------------섹션 엔티티 객체 생성 및 매핑---------------------------------------");
		// 섹션 엔티티 객체 생성 및 매핑
		List<LectureSection> savedlectureSeciton = lectureService.saveLectureSection(savedlecture,
				requestDto.getLectureSectionList());
		System.out.println("----------------------------비디오 메타 데이터 조회하기---------------------------------------");
		// 비디오 엔티티 객체 생성 및 매핑
		//uploadedVideos = youTubeVideoInfo.getVideoInfo(uploadedVideos, httpServletResponse, googleAccessToken);
		System.out.println("----------------------------비디오 엔티티 객체 생성 및 매핑---------------------------------------");
		
		for (VideoDto videoDto : uploadedVideos) {
			for (LectureSection section : savedlectureSeciton) {
				if (videoDto.getSectionNumber() == section.getSectionNumber()
						&& videoDto.getSectionTitle().equals(section.getSectionTitle())) {
					lectureService.saveVideo(savedlecture, section, videoDto);
				}
			}
		}
		lectureService.fullintrosave(savedlecture, requestDto.getLectureFullIntro());
		return "수신완료"; // Redirect to a success page
	}

	@PostMapping("/videotime")
	public void videotime(@RequestParam(name = "file") MultipartFile videofile) {
		System.out.println("originalfilename : " + videofile.getOriginalFilename());
		File file = new File("/home/ubuntu/devrun/temp/" + videofile.getOriginalFilename());
		try {
			videofile.transferTo(file);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			double sdf;
			sdf = durationExtractor.extract(file);
			System.err.println("총 재생 시간 : " + (int) sdf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JCodecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		file.delete();
//		try {
//			FileChannelWrapper channel = NIOUtils.readableChannel(file);
//			FrameGrab frameGrab = FrameGrab.createFrameGrab(channel);
//			double durationInSeconds = frameGrab.getVideoTrack().getMeta().getTotalDuration();
//			System.err.println("총 재생 시간 : " + (int)durationInSeconds);
//			channel.close();
//			file.delete();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JCodecException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}