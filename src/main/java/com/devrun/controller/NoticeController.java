package com.devrun.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devrun.dto.NoticeDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.Notice;
import com.devrun.service.NoticeService;

@Controller
public class NoticeController {

	@Autowired
	private NoticeService noticeService;
	
	// 공지사항 작성
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
		        
		        // MemberEntity가 null인지 확인
		        if (memberEntity == null) {
		            return ResponseEntity.status(400).body("MemberEntity not found");
		        }
		        
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
	        return ResponseEntity.status(500).body("Internal Server Error");
	    }
	}

	// 공지사항 단순 리스트
	@ResponseBody
    @GetMapping("/notice/list")
    public List<NoticeDTO> noticeList() {
        List<Notice> notices = noticeService.getNoticeList();
        return notices.stream().map(Notice::toDTO).collect(Collectors.toList());
    }
	
	// 공지사항 페이징
	@GetMapping("/notices")
    public ResponseEntity<Page<NoticeDTO>> getAllNotices(Pageable pageable) {
        Page<NoticeDTO> noticeDTOs = noticeService.getAllNotices(pageable);
        return new ResponseEntity<>(noticeDTOs, HttpStatus.OK);
    }
	
	// 공지사항 읽기
	@ResponseBody
	@GetMapping("/notice/detail/{noticeNo}")
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
	
	// 공지사항 수정
	@PutMapping("/notice/edit/{noticeNo}")
	public ResponseEntity<?> updateNotice(@PathVariable int noticeNo, @RequestBody NoticeDTO noticeDTO) {
	    try {
	        String newTitle = noticeDTO.getTitle();
	        String newContent = noticeDTO.getContent();
	        
	        noticeService.updateNotice(noticeNo, newTitle, newContent);
	        return ResponseEntity.status(200).body("Successfully updated notice with ID: " + noticeNo);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(400).body("Invalid arguments");
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body("Internal Server Error");
	    }
	}

}
