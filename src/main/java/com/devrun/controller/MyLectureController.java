package com.devrun.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.MyLectureNoteDTO;
import com.devrun.dto.MycouresDTO;
import com.devrun.dto.MylectureDTO;
import com.devrun.dto.NoteRequest;
import com.devrun.dto.NoteUpdateRequest;
import com.devrun.dto.QaDTO;
import com.devrun.dto.QaRequest;
import com.devrun.dto.ReviewRequest;
import com.devrun.dto.lectureNoteDetailDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.MylectureQa;
import com.devrun.service.MemberService;
import com.devrun.service.MyLectureService;
import com.devrun.service.MylectureReviewService;
import com.devrun.youtube.Lecture;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Api(tags = "MyLecture")
public class MyLectureController {

	private final MemberService memberService;
	private final MyLectureService mylectureService;
	private final MylectureReviewService reviewService;

	/*
	 * . 학생 유저가 작성한 강의 노트 가져오기(READ) 강의 ID를 받아서 노트를 가져오기 . 학생 유저가 작성한 강의 노트
	 * 저장하기(CREATE) 유저 정보, 강의 ID를 받아와서 DB에 저장 . 학생 유저가 작성한 강의 노트를 수정하기(UPDATE) 강의 노트
	 * ID를 받아서 수정된 데이터를 DB에 저장 . 학생 유저가 작성한 강의 노트를 삭제하기(DELETE) 강의 노트 ID를 받아와서 비활성화
	 * 처리
	 * 
	 * . 학생 유저가 작성한 질문글 가져오기(READ) 강의 ID를 받아서 질문글 가져오기 . 학생 유저가 작성한 질문글 작성하기(CREATE)
	 * 강의 ID를 받아서 질문글 받아와서 DB에 저장 . 학생 유저가 작성한 질문글 수정하기(UPDATE) 질문글 ID를 받아서 수정된 데이터를
	 * DB에 저장 . 학생 유저가 작성한 질문글 삭제하기(DELETE) 질문글 ID를 받아와서 비활성화 처리
	 * 
	 * . 학생 유저가 학습 중인 영상에 대한 재생 진행률 가져오기(READ) . 학생 유저가 학습 중인 영상에 대한 재생 진행률
	 * 저장하기(UPDATE)
	 * 
	 * . 학생 유저가 수강 후기글 작성하기(CREATE) 강의 ID를 받아오고 모든 영상을 시청했는 지 검증하고, 수강 후기글을 작성하고 평점을
	 * 매기면 , Lecture의 rating property에 update 필요
	 */

	/*
	 * 학생 유저가 수강 중인 강의 가져오기(READ) 강의 정보 - 섹션 정보 - 비디오 정보
	 */
	@GetMapping("/getMycoures")
	@ApiOperation(value = "학생 유저가 수강 중인 강의 커리큘럼 가져오기") // Api 설명
	public MycouresDTO getMycoures(HttpServletRequest request,
			@RequestParam(name = "lectureId", required = true) Long lectureId) {
		// String userAccessToken = request.getHeader("Access_token");
		// String userId = JWTUtil.getUserIdFromToken(userAccessToken);

		MemberEntity userEntity = memberService.findById("seokhwan2");
		MycouresDTO myCoures = mylectureService.findMycoures(userEntity, lectureId);
		return myCoures;
	}

	@GetMapping("/mylecturelist")
	@ApiOperation(value = "내 학습 불러오기", notes = "파라미터로 액세스 토큰과 강의 완강 여부(status), 페이지(page)를 요청할 수 있고, 각 페이지 당 10개의 항목이 반환됩니다.")
	@ApiImplicitParams({
			@ApiImplicitParam(example = "Inprogress", value = "강의 완강 여부", name = "status", dataTypeClass = String.class),
			@ApiImplicitParam(example = "1", value = "요청 페이지", name = "page", dataTypeClass = String.class) })
	public List<MylectureDTO> mylecturelist(HttpServletRequest httpServletRequest,
			@RequestParam(name = "status", required = false) String status,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		String accessToken = httpServletRequest.getHeader("Access_token");
		System.out.println("/mylecturelist Access_token" + accessToken);
		MemberEntity userEntity = memberService.findById("seokhwan2");

		return mylectureService.mylecturelist(userEntity, page);

	}

	@PostMapping("/reviewrating")
	@ApiOperation(value = "학습 후기 작성하고 평점 남기기", notes = "파라미터로 액세스 토큰과 강의 후기 내용(reviewContent), 강의 평점(reviewRating)를 제출합니다.")
	public String reviewProcess(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) ReviewRequest reviewRequest) {
//		String userId = JWTUtil.getUserIdFromToken(httpServletRequest.getHeader("Access_token"));
//		MemberEntity userEntity = memberService.findById(userId);
		MemberEntity userEntity = memberService.findById("seokhwan2");
		reviewService.saveReview(userEntity, reviewRequest);
		return "작성 완료";
	}

	@PostMapping("/lecture/progress")
	@ApiOperation(value = "영상 진행률 저장하기", notes = "파라미터로 액세스 토큰과 현재 시청중인 videoid(videoid)와, 현재 재생 누적 시간(currenttime)를 요청하면, 데이터베이스에 저장하고, 결과값을 반환합니다.")
	@ApiImplicitParams({
			@ApiImplicitParam(example = "w8-X2DED94A", value = "비디오 아이디", name = "videoid", dataTypeClass = String.class),
			@ApiImplicitParam(example = "120", value = "영상 재생 누적 시간(초 단위)", name = "currenttime", dataTypeClass = Integer.class) })
	public Map<String, Object> lectureprogress(HttpServletRequest httpServletRequest,
			@RequestParam("videoid") String videoid, @RequestParam("currenttime") int currenttime) {
//		String userId = JWTUtil.getUserIdFromToken(httpServletRequest.getHeader("Access_token"));
//		MemberEntity userEntity = memberService.findById(userId);
		System.out.println("------------------------------------영상 진행률 저장하기------------------------------------");
		MemberEntity userEntity = memberService.findById("seokhwan2");

		return mylectureService.progress(userEntity, videoid, currenttime);

	}

	@PostMapping("/lecturenote")
	@ApiOperation(value = "강의 노트 저장하기", notes = "강의 영상을 보던 중 작성한 강의 노트를 저장합니다.")
	public void lectureNoteSave(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) NoteRequest noteRequest) {
//		String userId = JWTUtil.getUserIdFromToken(httpServletRequest.getHeader("Access_token"));
//		MemberEntity userEntity = memberService.findById(userId);
		MemberEntity userEntity = memberService.findById("seokhwan2");
		mylectureService.myNoteSave(userEntity, noteRequest);
	}
	
	@PostMapping("/lecturenoteUpdate")
	@ApiOperation(value = "강의 노트 수정하기", notes = "작성한 강의 노트를 수정합니다.")
	public void lectureNoteSave(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) NoteUpdateRequest noteRequest) {
//		String userId = JWTUtil.getUserIdFromToken(httpServletRequest.getHeader("Access_token"));
//		MemberEntity userEntity = memberService.findById(userId);
		MemberEntity userEntity = memberService.findById("seokhwan2");
		mylectureService.myNoteUpdate(userEntity, noteRequest);
	}

	@GetMapping("/lectureNoteOpen")
	@ApiOperation(value = "강의별 노트 리스트 불러오기", notes = "유저가 작성한 강의 노트를 불러옵니다.")
	public List<MyLectureNoteDTO> lectureNoteOpen(HttpServletRequest httpServletRequest,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page) {
//		String userId = JWTUtil.getUserIdFromToken(httpServletRequest.getHeader("Access_token"));
//		MemberEntity userEntity = memberService.findById(userId);
		MemberEntity userEntity = memberService.findById("seokhwan2");
		return mylectureService.myNotelist(userEntity, page);
	}

	@GetMapping("/lectureNoteDetailOpen")
	@ApiOperation(value = "특정 강의의 노트 불러오기", notes = "유저가 선택한 강의에 대한 노트를 불러옵니다.")
	public List<lectureNoteDetailDTO> lectureNoteDetailOpen(HttpServletRequest httpServletRequest ,
	@RequestParam(name = "lectureId" , defaultValue = "1" , required = false) Long lectureId ,
	@RequestParam(name = "page", defaultValue = "0", required = false)	int page)
	{
//		String userId = JWTUtil.getUserIdFromToken(httpServletRequest.getHeader("Access_token"));
//		MemberEntity userEntity = memberService.findById(userId);
		MemberEntity userEntity = memberService.findById("seokhwan2");
		return mylectureService.noteDetaiList(userEntity, lectureId, page);
	}

	@PostMapping("/lectureQa")
	@ApiOperation(value = "강의 질문 올리기", notes = "유저가 강의에 대한 질문을 게시판에 게시")
	public void lectureQaSave(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) QaRequest qaRequest) {
//		String userId = JWTUtil.getUserIdFromToken(httpServletRequest.getHeader("Access_token"));
//		MemberEntity userEntity = memberService.findById(userId);
		MemberEntity userEntity = memberService.findById("seokhwan2");
		mylectureService.QaSave(userEntity, qaRequest);

	}

	@GetMapping("/lectureQaOpen")
	@ApiOperation(value = "강의 질문 가져오기", notes = "유저가 올린 질문을 가져옵니다.")
	public List<QaDTO> lectureQaList(HttpServletRequest httpServletRequest,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page) {
//		String userId = JWTUtil.getUserIdFromToken(httpServletRequest.getHeader("Access_token"));
//		MemberEntity userEntity = memberService.findById(userId);
		MemberEntity userEntity = memberService.findById("seokhwan2");
		return mylectureService.Qalist(userEntity, page);

	}
	
	@GetMapping("/certificates")
	@ApiOperation(value = "수료증 자격 확인" , notes = "수료증 자격을 확인합니다.")
	public String lectureCertificates(HttpServletRequest httpServletRequest , @RequestParam(name = "lectureId", defaultValue = "22", required = false) Long lectureId) {
//		String userId = JWTUtil.getUserIdFromToken(httpServletRequest.getHeader("Access_token"));
//		MemberEntity userEntity = memberService.findById(userId);
		MemberEntity userEntity = memberService.findById("seokhwan2");
		return mylectureService.checkLectureComplete(userEntity, lectureId);
	}
	
	@GetMapping("/save")
	public void lectureSave() {
		MemberEntity userEntity = memberService.findById("sunho1234");
		mylectureService.registLecture(userEntity, 22L);
	}
}
