package com.devrun.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.CommentDTO;
import com.devrun.dto.QaCommentQuest;
import com.devrun.dto.QaCommentUpdateDto;
import com.devrun.dto.QaDetailDTO;
import com.devrun.dto.QaListDTOs;
import com.devrun.dto.QaRequest;
import com.devrun.dto.QaUpdateRequest;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.MylectureQaAnswer;
import com.devrun.service.MemberService;
import com.devrun.service.MyLectureService;
import com.devrun.util.JWTUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Api(tags = "질문_답변 API")
public class QnAController {
	private final MemberService memberService;
	private final MyLectureService mylectureService;

	@PostMapping("/lectureQa")
	@ApiOperation(value = "강의 질문 올리기", notes = "유저가 강의에 대한 질문을 게시판에 게시")
	public QaDetailDTO lectureQaSave(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) QaRequest qaRequest) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.QaSave(userEntity, qaRequest);
	}

	@GetMapping("/lectureQaDetailOpen")
	@ApiOperation(value = "질문 디테일 가져오기", notes = "질문 디테일을 가져옵니다.")
	public QaDetailDTO lectureQaList(HttpServletRequest httpServletRequest,
			@RequestParam(name = "questionId") Long questionId) {

		return mylectureService.getQaDetail(questionId);

	}

	@GetMapping("/lectureQaCommentDetailOpen")
	@ApiOperation(value = "질문 디테일의 댓글 가져오기", notes = "질문 디테일의 댓글 가져옵니다.")
	public List<CommentDTO> lectureQaCommentList(HttpServletRequest httpServletRequest,
			@RequestParam(name = "questionId") Long questionId) {
		List<MylectureQaAnswer> list = mylectureService.getActiveCommentsByNotice(questionId);
		List<CommentDTO> commentDTOs = list.stream().map(MylectureQaAnswer::toDTO).collect(Collectors.toList());
		return commentDTOs;
	}

	@GetMapping("/lectureQalistOpen")
	@ApiOperation(value = "강의 질문 목록 가져오기", notes = "영상을 시청하는 화면에서 해당 강의 질문 목록을 가져옵니다.")
	public QaListDTOs lectureQaListBylecture(HttpServletRequest httpServletRequest,
			@RequestParam(name = "lectureId") Long lectureId,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page) {
		return mylectureService.QalistBylecture(lectureId, page);

	}

	@GetMapping("/mylectureQalistOpen")
	@ApiOperation(value = "유저가 질문한 질문 목록 가져오기", notes = "로그인한 유저가 작성한 질문 목록을 가져옵니다.")
	@ApiImplicitParams({
			@ApiImplicitParam(example = "waiting", value = "답변 여부 (completed or waiting)", name = "status", dataTypeClass = String.class),
			@ApiImplicitParam(example = "1", value = "요청 페이지", name = "page", dataTypeClass = String.class) })
	public QaListDTOs lectureQaListBylecture(HttpServletRequest httpServletRequest,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "status", defaultValue = "waiting", required = false) String status) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.QalistByMemberHandler(userEntity, page, status);

	}

	@GetMapping("/mylectureQalistOpen/seach")
	@ApiOperation(value = "유저가 질문한 질문 검색하기", notes = "로그인한 유저가 작성한 질문을 검색합니다.")
	@ApiImplicitParams({
			@ApiImplicitParam(example = "waiting", value = "답변 여부 (completed or waiting)", name = "status", dataTypeClass = String.class),
			@ApiImplicitParam(example = "1", value = "요청 페이지", name = "page", dataTypeClass = String.class),
			@ApiImplicitParam(example = "질문", value = "검색어", name = "keyword", dataTypeClass = String.class) })
	public QaListDTOs lectureQaListBylecture(HttpServletRequest httpServletRequest,
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "status", defaultValue = "", required = false) String status,
			@RequestParam(name = "keyword", defaultValue = "", required = false) String keyword) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.QalistBySearch(userEntity, page, status, keyword);

	}

	@PostMapping("/lectureQa/update")
	@ApiOperation(value = "작성한 질문을 수정합니다.")
	public QaDetailDTO lectureQaUpdate(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) QaUpdateRequest qaRequest) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.QaUpdate(userEntity, qaRequest);
	}

	@DeleteMapping("/lectureQa/delete")
	@ApiOperation(value = "작성한 질문을 삭제합니다.")
	public String lectureQaUpdate(HttpServletRequest httpServletRequest,
			@RequestParam(name = "questionId", required = true) Long questionId) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.QaDelete(userEntity, questionId);
	}

	// 댓글 작성
	@PostMapping("/lectureQaComment")
	@ApiOperation(value = "질문 디테일의 댓글 작성", notes = "질문 디테일에 댓글을 작성합니다.")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "noticeNo", value = "공지사항 번호", required = true, paramType = "body", dataTypeClass = Integer.class, example = "0"),
			@ApiImplicitParam(name = "content", value = "댓글 내용", required = true, paramType = "body", dataTypeClass = String.class),
			@ApiImplicitParam(name = "id", value = "작성자 아이디", required = true, paramType = "body", dataTypeClass = String.class) })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "댓글이 성공적으로 작성되었습니다."),
			@ApiResponse(code = 400, message = "잘못된 공지사항 번호입니다.") })
	public CommentDTO writeComment(HttpServletRequest httpServletRequest, @RequestBody QaCommentQuest commentQuest) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.writeComment(userEntity, commentQuest);
	}

	@DeleteMapping("/lectureQa/comment/delete/{commentId}")
	@ApiOperation(value = "작성한 질문에 대한 댓글을 삭제합니다.")
	public String lectureQaanswerDelete(HttpServletRequest httpServletRequest, @PathVariable int commentId) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.QaCommentDelete(userEntity, commentId);
	}

	@PostMapping("/lectureQa/comment/edit/{commentNo}")
	@ApiOperation(value = "작성한 질문에 대한 댓글을 수정합니다.")
	public String lectureQaanswerUpdate(HttpServletRequest httpServletRequest, @PathVariable int commentId,
			@RequestBody QaCommentUpdateDto dto) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		return mylectureService.QaCommentUpdate(userEntity, commentId, dto);
	}

}
