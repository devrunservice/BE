package com.devrun.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.CertificateDto;
import com.devrun.dto.MycouresDTO;
import com.devrun.dto.MylectureDTO2;
import com.devrun.dto.ProgressInfo;
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
@Api(tags = "학습관리 기타 API")
public class MyLectureController {

	private final MemberService memberService;
	private final MyLectureService mylectureService;
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

	@PostMapping("/lecture/progress")
	@ApiOperation(value = "영상 진행률 저장하기", notes = "파라미터로 액세스 토큰과 현재 시청중인 videoid(videoid)와, 현재 재생 누적 시간(currenttime)를 요청하면, 데이터베이스에 저장하고, 결과값을 반환합니다.")
	public Map<String, Object> lectureprogress(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) ProgressInfo prgressinfo) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		System.out.println("Currenttime : "+prgressinfo.getCurrenttime());
		System.out.println("Videoid : "+prgressinfo.getVideoid());
		return mylectureService.progress(userEntity, prgressinfo.getVideoid(), prgressinfo.getCurrenttime());

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

	@GetMapping("/certificates/print")
	@ApiOperation(value = "수료증 자격 확인", notes = "강의 Id를 보내면 수료 자격을 확인합니다.")
	public CertificateDto printCertificates(HttpServletRequest httpServletRequest,
			@RequestParam(name = "lectureId" , required = true) Long lectureId) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.printCertificates(userEntity, lectureId);
	}
}
