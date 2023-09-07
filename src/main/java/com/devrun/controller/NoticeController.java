package com.devrun.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devrun.dto.NoticeDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.Notice;
import com.devrun.service.NoticeService;
import com.devrun.util.TextChange;

@Controller
public class NoticeController {

	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private TextChange textChange;
	
	// 공지사항 작성 						아직 테스트
	@Transactional
	@ResponseBody
	@PostMapping("/notice/write")
	public ResponseEntity<?> notice(@RequestBody NoticeDTO noticeDTO) {
		try {
		    System.out.println("noticeDTO : " + noticeDTO);
		    String title = 
//		    		textChange.changeText(
		    				noticeDTO.getTitle()
//		    				)
		    		;
		    String content = 
//		    		textChange.changeText(
		    				noticeDTO.getContent()
//		    				)
		    		;
		    // 기존 Notice 찾기, 없으면 새로 생성
		    Notice notice = noticeService.findByNoticeNo(noticeDTO.getNoticeNo());
		    System.out.println("notice : " + notice);
	
		    if (notice == null) {
		        notice = new Notice();
		        notice.setNoticeNo(noticeDTO.getNoticeNo());
		        
		        // 관련된 MemberEntity 찾기
		        MemberEntity memberEntity = noticeService.findById(noticeDTO.getId());
		        
		        // Notice 엔터티에 MemberEntity 설정
		        notice.setMemberEntity(memberEntity);
		        
		    }
	
		    System.out.println("notice2 : " + notice);
	
		    // 필요한 필드 업데이트
		    notice.setTitle(title);
		    notice.setContent(content);
	
		    // 업데이트 또는 삽입
		    noticeService.insert(notice);
	
		    return ResponseEntity.status(200).body("ok");
		} catch (Exception e) {
	        return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
	    }
	}
	
	@ResponseBody
	@GetMapping("/notice/list")
	public ResponseEntity<List<NoticeDTO>> noticeList() {
	    try {
	        List<Notice> notices = noticeService.getNoticeList();
	        System.out.println("되나 : " + notices);
	        List<NoticeDTO> noticeDTOs = notices.stream().map(Notice::toDTO).collect(Collectors.toList());
	        return new ResponseEntity<>(noticeDTOs, HttpStatus.OK);
	    } catch (Exception e) {
	        // 예외 처리
	    	e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}



}
