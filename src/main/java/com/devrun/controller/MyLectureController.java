package com.devrun.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.CertificateDto;
import com.devrun.dto.MyLectureNoteDTO2;
import com.devrun.dto.MycouresDTO;
import com.devrun.dto.MylectureDTO2;
import com.devrun.dto.MylectureReviewDTO;
import com.devrun.dto.NoteRequest;
import com.devrun.dto.NoteUpdateRequest;
import com.devrun.dto.ProgressInfo;
import com.devrun.dto.QaDetailDTO;
import com.devrun.dto.QaListDTOs;
import com.devrun.dto.QaRequest;
import com.devrun.dto.ReviewRequest;
import com.devrun.dto.lectureNoteDetailDTO;
import com.devrun.dto.lectureNoteListDTO2;
import com.devrun.entity.MemberEntity;
import com.devrun.service.MemberService;
import com.devrun.service.MyLectureService;
import com.devrun.service.MylectureReviewService;
import com.devrun.util.JWTUtil;

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
	public MycouresDTO getMycoures(HttpServletRequest httpServletRequest,
			@RequestParam(name = "lectureId", required = true) Long lectureId) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		MycouresDTO myCoures = mylectureService.findMycoures(userEntity, lectureId);
		return myCoures;
	}

	@GetMapping("/mylecturelist")
	@ApiOperation(value = "내 학습 불러오기", notes = "파라미터로 액세스 토큰과 강의 완강 여부(status), 페이지(page)를 요청할 수 있고, 각 페이지 당 10개의 항목이 반환됩니다. 정렬순서는 최근 학습 순입니다.")
	@ApiImplicitParams({
			@ApiImplicitParam(example = "All", value = "강의 완강 여부", name = "status", dataTypeClass = String.class),
			@ApiImplicitParam(example = "1", value = "요청 페이지", name = "page", dataTypeClass = String.class) })
	public MylectureDTO2 mylecturelist(HttpServletRequest httpServletRequest,
			@RequestParam(name = "status", required = false, defaultValue = "All") String status,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);

		return mylectureService.mylecturelist(userEntity, page, status);

	}

	@PostMapping("/lastvideo")
	@ApiOperation(value = "해당 강의의 최근 영상 ID", notes = "강의 id를 요청하면 가장 최근에 학습했던 영상의 Id를 반환합니다.")
	public String lastvideo(HttpServletRequest httpServletRequest, @RequestBody(required = true) Long lectureId) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.lastvideo(userEntity, lectureId);
	}

	@PostMapping("/reviewrating")
	@ApiOperation(value = "학습 후기 작성하고 평점 남기기", notes = "파라미터로 액세스 토큰과 강의 후기 내용(reviewContent), 강의 평점(reviewRating)를 제출합니다.")
	public String reviewProcess(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) ReviewRequest reviewRequest) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		reviewService.saveReview(userEntity, reviewRequest);
		return "작성 완료";
	}
	
	@GetMapping("/review/{lectureId}/{pageNumber}")
	@ApiOperation(value = "해당 강의에 대한 수강평 보기", notes = "파라미터로 lectureId를 요청하면 해당 강의의 수강평을 반환합니다.")
	public List<MylectureReviewDTO> reviewList(@PathVariable Long lectureId , @PathVariable int pageNumber) {
		return reviewService.reviewList(lectureId , pageNumber);
	}
	

	@PostMapping("/lecture/progress")
	@ApiOperation(value = "영상 진행률 저장하기", notes = "파라미터로 액세스 토큰과 현재 시청중인 videoid(videoid)와, 현재 재생 누적 시간(currenttime)를 요청하면, 데이터베이스에 저장하고, 결과값을 반환합니다.")
	public Map<String, Object> lectureprogress(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) ProgressInfo prgressinfo) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);

		return mylectureService.progress(userEntity, prgressinfo.getVideoid(), prgressinfo.getCurrenttime());

	}

	@PostMapping("/lecturenote")
	@ApiOperation(value = "강의 노트 저장하기", notes = "강의 영상을 보던 중 작성한 강의 노트를 저장합니다.")
	public void lectureNoteSave(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) NoteRequest noteRequest) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		mylectureService.myNoteSave(userEntity, noteRequest);
	}

	@PostMapping("/lecturenoteUpdate")
	@ApiOperation(value = "강의 노트 수정하기", notes = "작성한 강의 노트를 수정합니다.")
	public lectureNoteDetailDTO lectureNoteUpdate(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) NoteUpdateRequest noteRequest) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.myNoteUpdate(userEntity, noteRequest);
	}

	@PostMapping("/lecturenoteDelete")
	@ApiOperation(value = "강의 노트 삭제하기", notes = "작성한 강의 노트를 삭제합니다.")
	public void lectureNoteDelte(HttpServletRequest httpServletRequest, @RequestBody(required = true) Long noteNo) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		mylectureService.myNoteDelete(userEntity, noteNo);
	}

	@GetMapping("/lectureNoteOpen")
	@ApiOperation(value = "전체 강의 리스트와 노트 갯수 불러오기", notes = "유저가 수강 중인 전체 강의와 노트 수를 출력합니다.")
	public MyLectureNoteDTO2 lectureNoteOpen(HttpServletRequest httpServletRequest,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.myNotelist(userEntity, page);
	}

	@GetMapping("/lectureNoteListOpen")
	@ApiOperation(value = "선택한 강의에서 작성한 노트 리스트 미리보기 불러오기", notes = "선택한 강의에서 작성한 노트 리스트 미리보기 불러오기")
	public lectureNoteListDTO2 lectureNoteListOpen(HttpServletRequest httpServletRequest,
			@RequestParam(name = "lectureId", defaultValue = "1", required = false) Long lectureId,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.noteDetaiList(userEntity, lectureId, page);
	}

	@GetMapping("/lectureNoteDetailOpen")
	@ApiOperation(value = "선택한 노트의 세부 내용 불러오기", notes = "유저가 선택한 노트 한개에 대한 세부 내용을 불러옵니다.")
	public lectureNoteDetailDTO lectureNoteDetailOpen(HttpServletRequest httpServletRequest,
			@RequestParam(name = "noteId", defaultValue = "1", required = true) Long noteNo) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.noteDetaiOpen(userEntity, noteNo);
	}

	@PostMapping("/lectureQa")
	@ApiOperation(value = "강의 질문 올리기", notes = "유저가 강의에 대한 질문을 게시판에 게시")
	public void lectureQaSave(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) QaRequest qaRequest) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		mylectureService.QaSave(userEntity, qaRequest);

	}

	@GetMapping("/lectureQaDetailOpen/{questionId}")
	@ApiOperation(value = "질문 디테일 가져오기", notes = "질문 디테일을 가져옵니다.")
	public QaDetailDTO lectureQaList(HttpServletRequest httpServletRequest,
			@PathVariable(name = "questionId") Long questionId) {
		return mylectureService.getQaDetail(questionId);

	}
	
	@GetMapping("/lectureQalistOpen")
	@ApiOperation(value = "강의 질문 목록 가져오기", notes = "영상을 시청하는 화면에서 해당 강의 질문 목록을 가져옵니다.")
	public QaListDTOs lectureQaListBylecture(HttpServletRequest httpServletRequest, @RequestParam(name = "lectureId") Long lectureId,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page) {
		return mylectureService.QalistBylecture(lectureId,page);

	}
	
	@GetMapping("/mylectureQalistOpen")
	@ApiOperation(value = "유저가 질문한 질문 목록 가져오기", notes = "로그인한 유저가 작성한 질문 목록을 가져옵니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(example = "trueAnswer", value = "답변 여부 (trueAnswer or falseAnswer)", name = "answer", dataTypeClass = String.class),
		@ApiImplicitParam(example = "1", value = "요청 페이지", name = "page", dataTypeClass = String.class) })
	public QaListDTOs lectureQaListBylecture(HttpServletRequest httpServletRequest,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "answer", defaultValue = "", required = false) String sort) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.QalistByMemberHandler(userEntity,page,sort);

	}
	
	@GetMapping("/mylectureQalistOpen/seach")
	@ApiOperation(value = "유저가 질문한 질문 목록 가져오기", notes = "로그인한 유저가 작성한 질문 목록을 가져옵니다.")
	public QaListDTOs lectureQaListBylecture(HttpServletRequest httpServletRequest,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "answer", defaultValue = "", required = false) String sort,
			@RequestParam(name = "keyword", defaultValue = "", required = false) String keyword) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.QalistBySearch(userEntity,page,sort,keyword);

	}
	
	@PostMapping("/lectureQa/update")
	@ApiOperation(value = "작성한 질문을 수정합니다.")
	public QaDetailDTO lectureQaUpdate(HttpServletRequest httpServletRequest, @RequestBody(required = true) QaRequest qaRequest) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.QaUpdate(userEntity,qaRequest);
	}

	@GetMapping("/certificates")
	@ApiOperation(value = "수료한 강의 목록", notes = "수료증 자격을 확인하고, 수료증을 받을 수 있는 강의 목록을 출력")
	public MylectureDTO2 lectureCertificates(HttpServletRequest httpServletRequest,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.checkLectureComplete(userEntity, page);
	}

	@PostMapping("/certificates/print")
	@ApiOperation(value = "수료증 자격 확인", notes = "강의 Id를 보내면 수료 자격을 확인합니다.")
	public CertificateDto printCertificates(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) Long lectureId) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.printCertificates(userEntity, lectureId);
	}
}
