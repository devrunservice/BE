package com.devrun.youtube;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devrun.dto.QueryLectureByKeywordDTO;
import com.devrun.dto.TotalProgress;
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

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
public class LectureregistController {

	private final LectureService lectureService;
	private final AwsS3UploadService awsS3UploadService;
	private final YouTubeUploader youTubeUploader;
	private final LecutureCategoryService categoryService;
	private final MemberService memberService;
	private final MyLectureProgressService myLectureProgressService;

	public static final HttpTransport httpTransport = new NetHttpTransport();
	public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String CREDENTIALS_DIRECTORY = ".oauth-credentials";

	GoogleClientSecrets clientSecrets = loadClientSecretsFromFile(); // 파일로부터 클라이언트 비밀 정보 로드하는 예시 메서드
	private static final String redirectUri = "http://localhost:3000/auth/google/callback";
	
	@Autowired
	private MemberEntityRepository memberEntityRepository;
	
	@Autowired
	public LectureregistController(MemberService memberService, LectureService lectureService,
			AwsS3UploadService awsS3UploadService, YouTubeUploader youTubeUploader,
			LecutureCategoryService categoryService, MyLectureProgressService myLectureProgressService) {
		this.categoryService = categoryService;
		this.lectureService = lectureService;
		this.awsS3UploadService = awsS3UploadService;
		this.youTubeUploader = youTubeUploader;
		this.memberService = memberService;
		this.myLectureProgressService = myLectureProgressService;
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

	// POST 요청을 통해 강의 등록을 처리하는 엔드포인트
	@PostMapping("/lectureregist")
	public ResponseEntity<String> createLecture(@ModelAttribute CreateLectureRequestDto requestDto,
			@RequestParam("image") List<MultipartFile> imageFiles,
			@RequestParam("videoFileList") List<MultipartFile> videoFiles, @RequestParam("categoryNo") Long categoryNo,
			@RequestParam("lectureBigCategory") String lectureBigCategory,
			@RequestParam("lectureMidCategory") String lectureMidCategory,
			@RequestParam("accessToken") String accessToken) {
		try {
			if (videoFiles.isEmpty()) {
				return ResponseEntity.badRequest().body("동영상 파일을 선택해주세요.");
			}

			LecturecategoryDto categoryDto = new LecturecategoryDto();
			categoryDto.setLectureBigCategory(lectureBigCategory);
			categoryDto.setLectureMidCategory(lectureMidCategory);
			categoryDto.setCategoryNo(categoryNo);

			awsS3UploadService.putS3(imageFiles.get(0), "public.lecture.images", requestDto.getLectureName());

			// 강의 및 비디오 정보를 데이터베이스에 저장하고, 강의 썸네일 이미지를 S3에 업로드한 URL을 가져옴
			// Lecture savedLecture = lectureService.saveLecture(requestDto, imageUrls,
			// categoryDto);

			System.out.println("Number of video files to upload: " + videoFiles.size());

			// 동영상 업로드 및 정보 저장
//            List<VideoInfo> videoInfoList = new ArrayList<>();
//            for (MultipartFile videoFile : videoFiles) {
//                VideoInfo videoInfo = youTubeUploader.uploadVideo(videoFile, savedLecture.getId(), accessToken); // savedLecture의 ID와 엑세스 토큰 사용
//                System.out.println("아이디???? " + savedLecture.getId());
//                videoInfoList.add(videoInfo);
//            }

			// 동영상 정보를 데이터베이스에 저장
			// lectureService.saveVideoInfo(videoInfoList, savedLecture);

			// 동영상 업로드를 시작하기 위해 업로드 페이지로 리다이렉션
			return ResponseEntity.ok("강의 및 비디오 정보가 저장되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
		}
	}

	@PostMapping("/lectureregitest")
	public String lecturetest(@ModelAttribute CreateLectureRequestDto requestDto,
			@RequestParam("accessToken") String googleAccessToken, HttpServletResponse httpServletResponse , @RequestParam("jwtToken") String jwtToken)
			throws Exception {
		System.out
				.println("--------------------------------lectureregitest Controller --------------------------------");
		System.out.println(requestDto.getLectureName());
		System.out.println("accessToken :" + googleAccessToken);
		System.err.println(requestDto);
		// S3에가서 이미지를 업로드하고, 썸네일 URL 받아오기
		// Lecture save
		// LectureSection save
		// VideoDto + Lecuture, LectureSection = Video
		// List<Video> saveAll
		// List<VideoDto> videolist = requestDto.getVideoList();
//    	VideoDto videolist = requestDto.getVideoList();
		List<VideoDto> videolist = requestDto.getVideoList();
		System.err.println(videolist);

		// 업로드된 비디오 정보를 저장할 리스트를 생성합니다.
		List<VideoDto> uploadedVideos = new ArrayList<>();

		requestDto.setVideoList(videolist);
		
		
		  // JWT 토큰에서 사용자 아이디 추출
	    String userId = JWTUtil.getUserIdFromToken(jwtToken);
	    
	    // 멘토(사용자) 정보 조회
	    MemberEntity mento = memberEntityRepository.findById(userId);
	    
	    
	    // 강의 엔터리 객체 생성 및 매핑
	    Lecture lecture = new Lecture();
	    lecture.setMentoId(mento);
	    
	    
	    
		try {

			// 리스트의 각 비디오에 대해 업로드 작업을 수행합니다.
			for (VideoDto video : videolist) {
				VideoDto uploadedVideo = youTubeUploader.uploadVideo(video, httpServletResponse, googleAccessToken);
				uploadedVideos.add(uploadedVideo);
			}

			String lectureThumnailUrl = awsS3UploadService.putS3(requestDto.getLectureThumbnailFile(),
					"public.lecture.images", requestDto.getLectureName());

			Lecture savedlecture = lectureService.saveLecture2(requestDto, lectureThumnailUrl);

			List<LectureSection> savedlectureSeciton = lectureService.saveLectureSection(savedlecture,
					requestDto.getLectureSectionList());

			lectureService.saveVideo(savedlecture, savedlectureSeciton, videolist);
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

	// 필요 기능 : 페이지네이션 , 정렬 기능 , 통합 검색(강의명,강의소개,강사명)
	@GetMapping({ "/q/lecture" })
	@ApiImplicitParams({
		@ApiImplicitParam(example = "요리", value = "대분류 카테고리", name = "bigcategory" , dataTypeClass = String.class),
		@ApiImplicitParam(example = "라면", value = "중분류 카테고리", name = "midcategory" , dataTypeClass = String.class),
		@ApiImplicitParam(example = "sky", value = "검색 키워드", name = "q" , dataTypeClass = String.class),
		@ApiImplicitParam(example = "lecture_start", value = "정렬 옵션", name = "order" , dataTypeClass = String.class),
		@ApiImplicitParam(example = "1", value = "요청 페이지", name = "page" , dataTypeClass = String.class)
	})
	@ApiOperation(value = "강의 조회 API", notes = "파라미터로 키워드를 입력하면 강의를 반환합니다. 각 파라미터로 키워드, 정렬 옵션, 페이지 를 요청할 수 있고, 각 페이지 당 10개의 항목이 반환됩니다. 정렬 옵션은 lectureStart (등록날짜순) 또는 lecturePrice (가격순) 이며 추후 제약 조건들을 추가하고, 평점 기능이 도입되면 평점순도 추가할 예정입니다. 정렬 옵션을 입력하지 않으면 기본적으론 등록순이며 모든 정렬은 내림차순입니다.")
	public List<QueryLectureByKeywordDTO> testmethod1(
			@RequestParam(value = "bigcategory", defaultValue = "", required = false) String bigcategory,
			@RequestParam(value = "midcategory", defaultValue = "", required = false) String midcategory,
			@RequestParam(value = "q", defaultValue = "", required = false) String keyword,
			@RequestParam(value = "order", defaultValue = "lecture_start", required = false) String order,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page) {
		// 상황 1 : 검색어가 비어 있는 경우 - 모든 강의 리스트 조회
		// 상황 2 : 검색어가 존재하는 경우 - 통합 검색
		// 상황 3 : 카테고리 검색
		// 전처리 : 페이지네이션 -> 정렬(최신순,평점순,

		if (page == null || page <= 0) {
			page = 1;
		}
		Direction direction = Direction.DESC;
		PageRequest pageRequest = PageRequest.of(page - 1, 10, direction, order);

		Specification<Lecture> spec = (root, query, criteriaBuilder) -> null;

		// 카테고리 검색
		if (bigcategory.isEmpty() && midcategory.isEmpty()) { // 키워드 검색으로 이동
			System.out.println("bigcategory.isEmpty() && midcategory.isEmpty()");

		} else if (!bigcategory.isEmpty() && midcategory.isEmpty()) {// 대분류+(키워드) 검색

			List<LectureCategory> categorys = categoryService.findcategory(bigcategory);
			for (LectureCategory lectureCategory : categorys) {
				System.out.println(
						"----------------------------------------------" + lectureCategory.getLectureMidCategory());
			}
			List<QueryLectureByKeywordDTO> p1 = lectureService.findLecturesWithCategroys(categorys, keyword,
					pageRequest);
			return p1;
		} else if (!bigcategory.isEmpty() && !midcategory.isEmpty()) { // 대분류+중분류+(키워드) 검색
			LectureCategory category = categoryService.findcategory(bigcategory, midcategory);
			List<QueryLectureByKeywordDTO> p1 = lectureService.findLecturesWithCategroy(category, keyword, pageRequest);
			return p1;
		} else {
			List<LectureCategory> categorys = categoryService.findcategory(midcategory);
			List<QueryLectureByKeywordDTO> p1 = lectureService.findLecturesWithCategroys(categorys, keyword,
					pageRequest);
			return p1;
		} // 키워드 검색

		// 키워드 검색
		if (keyword.isEmpty()) {
			List<QueryLectureByKeywordDTO> p1 = lectureService.QueryLectureByKeyword(keyword, pageRequest);
			return p1;
		} else {
			List<MemberEntity> m1 = memberService.findByIdContains(keyword);
			if (m1.size() == 0) {
				List<QueryLectureByKeywordDTO> p1 = lectureService.QueryLectureByKeyword(keyword, pageRequest);
				return p1;
			} else {
				List<QueryLectureByKeywordDTO> p1 = lectureService.QueryLectureByKeyword(keyword, m1, pageRequest);
				return p1;
			}
		}

	}

	@PostMapping("/lectureregitest2")
	public String lecturetest23(@ModelAttribute CreateLectureRequestDto requestDto) {

		Lecture savedlecture = lectureService.saveLecture2(requestDto, "URL");
		List<LectureSection> savedlectureSeciton = lectureService.saveLectureSection(savedlecture,
				requestDto.getLectureSectionList());
		List<VideoDto> videolist = requestDto.getVideoList();
		lectureService.saveVideo(savedlecture, savedlectureSeciton, videolist);
		return null;

	}

	@PostMapping("/lecture/progress")
	@ApiOperation(value = "영상 진행률 저장하기", notes = "파라미터로 액세스 토큰과 현재 시청중인 videoid(videoid)와, 현재 재생 누적 시간(currenttime)를 요청하면, 데이터베이스에 저장하고, 결과값을 반환합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(example = "w8-X2DED94A",value = "비디오 아이디",name = "videoid" , dataTypeClass = String.class),
		@ApiImplicitParam(example = "120" ,value = "영상 재생 누적 시간(초 단위)",name = "currenttime" , dataTypeClass = Integer.class)				
			})
	public Map<String, Object> lectureprogress(HttpServletRequest httpServletRequest,
			@RequestParam("videoid") String videoid, @RequestParam("currenttime") int currenttime) {
		String accessToken = httpServletRequest.getHeader("Access_token");
		// String userid =
		// SecurityContextHolder.getContext().getAuthentication().getName();

		return myLectureProgressService.progress(accessToken, videoid, currenttime);

	}

	@GetMapping("/mylecturelist")
	@ApiOperation(value = "내 학습 불러오기", notes = "파라미터로 액세스 토큰과 강의 완강 여부(status), 페이지(page)를 요청할 수 있고, 각 페이지 당 10개의 항목이 반환됩니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(example = "Inprogress",value = "강의 완강 여부",name = "status" , dataTypeClass = String.class),
		@ApiImplicitParam(example = "1",value = "요청 페이지",name = "page" , dataTypeClass = String.class)				
			})
	public List<Map<String, Object>> mylecturelist(HttpServletRequest httpServletRequest , @RequestParam(name = "status" , required = false) String status, @RequestParam(name = "page" , required = false) String page) {
		String accessToken = httpServletRequest.getHeader("Access_token");
		System.out.println("/mylecturelist Access_token" + accessToken);
		
		
		return myLectureProgressService.mylecturelist(accessToken);

	}
	
	@GetMapping("/mylecturelisttest")
	public void mytest() {
		List<TotalProgress> fd = myLectureProgressService.mylecturelistReal(null);
		for (TotalProgress totalProgress : fd) {
			System.out.println(totalProgress.getlectureno());
			System.out.println(totalProgress.getuserno());			
		}
	}
	
	@GetMapping("/lectrueVideoOpen")
	public ResponseEntity<?> thisIsTestForVideoOpen(){
		Map<String , String> videoDTO = new HashMap<String, String>();
		videoDTO.put("videoId", "nNA-sbOzHl4");
		return ResponseEntity.ok(videoDTO);
	}
	
	@GetMapping("/lectrueNoteOpen")
	public ResponseEntity<?> thisIsTestForlectrueNoteOpen(@RequestParam(name = "page" , required = false , defaultValue = "0") String page){
		List<Map<String , String>> lectrueNoteDTOlist = new ArrayList<Map<String , String>>();
		for(int i = 1 ; i < 11 ; i++) {			
			Map<String , String> lectrueNoteDTO = new HashMap<String, String>();
			lectrueNoteDTO.put("lectureTitle", "노트를 작성한 강의 이름 : " + i);
			lectrueNoteDTO.put("chapter", "강의의 섹션 이름 : " + i);
			lectrueNoteDTO.put("subHeading", "소제목 : " + i);
			lectrueNoteDTO.put("noteTitle", "노트의 제목 : " + i);
			lectrueNoteDTO.put("count", "5 : " + i);
			lectrueNoteDTO.put("date", "2023-" + String.format("%02d" ,(int) ((Math.random() * 12) + 1)) + "-" + String.format("%02d" ,(int) ((Math.random() * 31) + 1)));
			lectrueNoteDTO.put("content", "<h1>This is a Heading 1</h1>"
					+ "    <h2>This is a Heading 2</h2>"
					+ "    <h3>This is a Heading 3</h3>"
					+ ""
					+ "    <p>This is a sample paragraph of text. It can contain multiple sentences.</p>"
					+ ""
					+ "    <p>This is <b>bold</b> text, and this is <i>italic</i> text.</p>"
					+ ""
					+ "    <p>This is <u>underlined</u> text.</p>"
					+ ""
					+ "    <p>This is <s>strikethrough</s> text.</p>"
					+ ""
					+ "    <p>This is <sup>superscript</sup> and this is <sub>subscript</sub> text.</p>"
					+ ""
					+ "    <hr>"
					+ ""
					+ "    <p>This is some text above the line.</p>"
					+ "    <hr>"
					+ "    <p>This is some text below the line.</p>"
					+ ""
					+ "    <blockquote>"
					+ "        <p>This is a blockquote. It can be used to display quoted text.</p>"
					+ "    </blockquote>"
					+ ""
					+ "    <p>This is some text.<br>Here's a line break.</p>"
					+ ""
					+ "    <pre>"
					+ "        This is preformatted text."
					+ "        It maintains the formatting and spacing."
					+ "    </pre>");
			lectrueNoteDTOlist.add(lectrueNoteDTO);
		}
		return ResponseEntity.ok(lectrueNoteDTOlist);
	}
	
	@GetMapping("/lectrueQnAOpen")
	public ResponseEntity<?> thisIsTestForlectrueQnAOpen(){
		List<Map<String , String>> lectrueQnADTOlist = new ArrayList<Map<String , String>>();
		for(int i = 1 ; i < 11 ; i++) {			
			Map<String , String> lectrueQnADTO = new HashMap<String, String>();
			lectrueQnADTO.put("lectureTitle", "질문을 작성한 강의 이름 : " + i);
			lectrueQnADTO.put("chapter", "강의의 섹션 이름 : " + i);
			lectrueQnADTO.put("subHeading", "소제목 : " + i);
			lectrueQnADTO.put("questionTitle", "질문의 제목 : " + i);
			lectrueQnADTO.put("date", "2023-" + String.format("%02d" ,(int) ((Math.random() * 12) + 1)) + "-" + String.format("%02d" ,(int) ((Math.random() * 31) + 1)));
			lectrueQnADTO.put("content", "<h1>This is a Heading 1</h1>"
					+ "    <h2>This is a Heading 2</h2>"
					+ "    <h3>This is a Heading 3</h3>"
					+ ""
					+ "    <p>This is a sample paragraph of text. It can contain multiple sentences.</p>"
					+ ""
					+ "    <p>This is <b>bold</b> text, and this is <i>italic</i> text.</p>"
					+ ""
					+ "    <p>This is <u>underlined</u> text.</p>"
					+ ""
					+ "    <p>This is <s>strikethrough</s> text.</p>"
					+ ""
					+ "    <p>This is <sup>superscript</sup> and this is <sub>subscript</sub> text.</p>"
					+ ""
					+ "    <hr>"
					+ ""
					+ "    <p>This is some text above the line.</p>"
					+ "    <hr>"
					+ "    <p>This is some text below the line.</p>"
					+ ""
					+ "    <blockquote>"
					+ "        <p>This is a blockquote. It can be used to display quoted text.</p>"
					+ "    </blockquote>"
					+ ""
					+ "    <p>This is some text.<br>Here's a line break.</p>"
					+ ""
					+ "    <pre>"
					+ "        This is preformatted text."
					+ "        It maintains the formatting and spacing."
					+ "    </pre>");
			lectrueQnADTOlist.add(lectrueQnADTO);
		}
		return ResponseEntity.ok(lectrueQnADTOlist);
	}
}