package com.devrun.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.MyLectureNoteDTO2;
import com.devrun.dto.NoteRequest;
import com.devrun.dto.NoteUpdateRequest;
import com.devrun.dto.lectureNoteDetailDTO;
import com.devrun.dto.lectureNoteListDTO2;
import com.devrun.entity.MemberEntity;
import com.devrun.service.MemberService;
import com.devrun.service.MyLectureService;
import com.devrun.service.MylectureReviewService;
import com.devrun.util.JWTUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Api(tags = "강의 노트 API")
public class NoteController {

	private final MemberService memberService;
	private final MyLectureService mylectureService;

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

	@DeleteMapping("/lecturenoteDelete")
	@ApiOperation(value = "강의 노트 삭제하기", notes = "작성한 강의 노트를 삭제합니다.")
	public void lectureNoteDelte(HttpServletRequest httpServletRequest,
			@RequestParam(name = "noteNo", defaultValue = "1", required = true) Long noteNo) {
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

}
