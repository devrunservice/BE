package com.devrun.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devrun.dto.NoticeDTO;
import com.devrun.dto.NoticeDTO.Status;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.Notice;
import com.devrun.service.NoticeService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
public class NoticeController {

	@Autowired
	private NoticeService noticeService;
	
	// 공지사항 작성
	@Transactional
	@ResponseBody
	@PostMapping("/notice/write")
	@ApiOperation(value = "공지사항 작성/수정", notes = "공지사항을 작성하거나 수정합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "title", value = "공지사항 제목", required = true, paramType = "body", dataTypeClass = String.class),
		@ApiImplicitParam(name = "content", value = "공지사항 내용", required = true, paramType = "body", dataTypeClass = String.class)})
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "공지사항 작성/수정 성공"),
	    @ApiResponse(code = 400, message = "잘못된 입력값 또는 MemberEntity를 찾을 수 없음"),
	    @ApiResponse(code = 500, message = "내부 서버 오류")})
	public ResponseEntity<?> notice(@RequestBody NoticeDTO noticeDTO) {
		try {
		    String title = noticeDTO.getTitle();
		    String content = noticeDTO.getContent();
		    
		    // 항상 새로운 Notice 객체를 생성
	        Notice notice = new Notice();
	        
	        // 관련된 MemberEntity 찾기
	        MemberEntity memberEntity = noticeService.findById(noticeDTO.getId());
	        
	        // MemberEntity가 null인지 확인
	        if (memberEntity == null) {
	            return ResponseEntity.status(400).body("MemberEntity not found");
	        }
	        
	        // Notice 엔터티에 MemberEntity 설정
	        notice.setMemberEntity(memberEntity);
	
		    // 필요한 필드 업데이트
		    notice.setTitle(title);
		    notice.setContent(content);
	
		    // 업데이트 또는 삽입
		    noticeService.insert(notice);
	
		    return ResponseEntity.status(200).body("ok");
		} catch (Exception e) {
	        return ResponseEntity.status(500).body("Internal Server Error");
	    }
	}

	// 공지사항 페이징
	@ResponseBody
	@GetMapping("/notices/{pageNumber}")
	@ApiOperation(value = "공지사항 페이징", notes = "페이지 번호에 따른 공지사항 목록을 반환합니다.")
	@ApiImplicitParam(name = "pageNumber", value = "페이지 번호", required = true, paramType = "path", dataTypeClass = Integer.class , example = "0")
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "공지사항 목록을 성공적으로 반환했습니다."),
	    @ApiResponse(code = 404, message = "공지사항이 없습니다.")})
	public ResponseEntity<Page<NoticeDTO>> getAllNotices(@PathVariable int pageNumber) {
	    // 'createdDate'를 기준으로 내림차순 정렬
	    Pageable pageable = PageRequest.of(pageNumber - 1, 10, Sort.by(
//	    		Sort.Direction.DESC, "createdDate")									// 열거형 - 다중 속성 설정하기에 좋음
	    		"createdDate").descending()											// 체인형 - 단일 속성에 여러 조건을 설정하기 좋음
	    		);
	    
	    // status가 'ACTIVE'인 공지사항만 가져옵니다.
	    Page<NoticeDTO> noticeDTOs = noticeService.getAllActiveNotices(pageable);
	    if (noticeDTOs == null) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	    
	    return new ResponseEntity<>(noticeDTOs, HttpStatus.OK);
	}
	
	// 공지사항 읽기
	@ResponseBody
	@GetMapping("/notices/detail/{noticeNo}")
	@ApiOperation(value = "공지사항 읽기", notes = "공지사항 번호에 따른 상세 정보를 반환합니다.")
	@ApiImplicitParam(name = "noticeNo", value = "공지사항 번호", required = true, paramType = "path", dataTypeClass = Integer.class , example = "0")
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "공지사항 상세 정보를 성공적으로 반환했습니다."),
	    @ApiResponse(code = 404, message = "공지사항을 찾을 수 없습니다."),
	    @ApiResponse(code = 500, message = "내부 서버 오류")})
	public ResponseEntity<?> getNotice(@PathVariable int noticeNo) {
	    try {
	        Notice notice = noticeService.getNoticeByNoticeNo(noticeNo);
	        System.out.println(notice);
	        if (notice == null) {
	            return ResponseEntity.status(404).body("Notice not found");
	        }
	        
	        // 조회수 증가
	        notice.setViewCount(notice.getViewCount() + 1);
	        noticeService.insert(notice);
	        
	        return ResponseEntity.status(200).body(notice.toDTO());
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body("Internal Server Error");
	    }
	}

	
	// 공지사항 읽기
		@ResponseBody
		@GetMapping("/notices/detail/{noticeNo}")
		@ApiOperation(value = "공지사항 읽기", notes = "공지사항 번호에 따른 상세 정보를 반환합니다.")
		@ApiImplicitParam(name = "noticeNo", value = "공지사항 번호", required = true, paramType = "path", dataTypeClass = Integer.class , example = "0")
		@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "공지사항 상세 정보를 성공적으로 반환했습니다."),
		    @ApiResponse(code = 404, message = "공지사항을 찾을 수 없습니다."),
		    @ApiResponse(code = 500, message = "내부 서버 오류")})
		public ResponseEntity<?> getNotice(@PathVariable int noticeNo, HttpServletRequest request, HttpServletResponse response) {
		    try {
		        Notice notice = noticeService.getNoticeByNoticeNo(noticeNo);
		        System.out.println(notice);
		        if (notice == null) {
		            return ResponseEntity.status(404).body("Notice not found");
		        }
		        Cookie[] cookies = request.getCookies();
		        Cookie cookie = null;
		        boolean isCookie = false;

		        for (int i = 0; cookies != null && i < cookies.length; i++) {
		            if (cookies[i].getName().equals("postView")) {
		                cookie = cookies[i];

		                // 쿠키 값을 파싱하여 이미 본 게시물인지 확인
		                String cookieValue = cookie.getValue();
		                if (!cookieValue.contains("[" + notice.getNoticeNo() + "]")) {
		                    // 조회수 증가
		                    notice.setViewCount(notice.getViewCount() + 1);
		                    noticeService.insert(notice);

		                    // 쿠키 값에 해당 게시글 번호를 추가
		                    cookie.setValue(cookieValue + "[" + notice.getNoticeNo() + "]");
		                }

		                isCookie = true;
		                break;
		            }
		        }

		        // 쿠키가 없거나 새로 생성하는 경우
		        if (!isCookie) {
		            // 조회수 증가
		            notice.setViewCount(notice.getViewCount() + 1);
		            noticeService.insert(notice);

		            cookie = new Cookie("postView", "[" + notice.getNoticeNo() + "]");
		        }

		        // 쿠키 유지시간을 오늘 하루 자정까지로 설정
		        LocalDate tomorrow = LocalDate.now().plusDays(1);
		        LocalDateTime midnight = tomorrow.atStartOfDay();
		        long secondsUntilMidnight = midnight.toEpochSecond(ZoneOffset.UTC);
		        long currentSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

		        cookie.setPath("/");
		        cookie.setMaxAge((int) (secondsUntilMidnight - currentSecond));
		        response.addCookie(cookie);

		        return ResponseEntity.status(200).body(notice.toDTO());
		    } catch (Exception e) {
		        return ResponseEntity.status(500).body("Internal Server Error");
		    }
		}

	// 공지사항 삭제 (실제로는 Status를 INACTIVE로 변경)
	@ResponseBody
	@DeleteMapping("/notice/delete/{noticeNo}")
	@ApiOperation(value = "공지사항 삭제", notes = "공지사항의 상태를 INACTIVE로 변경합니다.")
	@ApiImplicitParam(name = "noticeNo", value = "삭제할 공지사항 번호", required = true, paramType = "path", dataTypeClass = Integer.class , example = "0")
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "공지사항을 성공적으로 삭제했습니다."),
	    @ApiResponse(code = 400, message = "잘못된 인수입니다."),
	    @ApiResponse(code = 500, message = "내부 서버 오류")})
	public ResponseEntity<?> deleteNotice(@PathVariable int noticeNo) {
	    try {
	        noticeService.setStatus(noticeNo, Status.INACTIVE);
	        return ResponseEntity.status(200).body("Successfully deleted notice with ID: " + noticeNo);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(400).body("Invalid arguments");
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body("Internal Server Error");
	    }
	}
}
