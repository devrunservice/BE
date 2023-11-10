package com.devrun.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.entity.MemberEntity;
import com.devrun.service.MemberService;
import com.devrun.service.TestService;
import com.devrun.util.CaffeineCache;
import com.devrun.util.JWTUtil;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {
	
	@Autowired
	TestService testService;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
    private CaffeineCache redisCache;
//    private RedisCache redisCache;
	
//	122.41.29.73
//	@CrossOrigin(origins = "localhost:3000" , allowedHeaders = {"GET"})
	@GetMapping("/tmi")
	public ResponseEntity<?> tmi(HttpServletRequest request) {
		// AccessToken이 헤더에 있는지 확인
		String accessToken = request.getHeader("Access_token");

	    // AccessToken 존재 여부 확인 (null 혹은 빈문자열 인지 확인)
		if (accessToken == null || accessToken.isEmpty()) {
			// 400 : Access token 없음
			return new ResponseEntity<>("Access token is required", HttpStatus.BAD_REQUEST);
		}
		
		String id = JWTUtil.getUserIdFromToken(accessToken);
		
//			if (memberService.isUserIdEquals(id)) {
				MemberEntity member = memberService.findById(id);
				return ResponseEntity.ok(member);
//			} else {
				// 401 토큰의 사용자와 요청한 사용자 불일치
//				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request");
//			}

	}

	@GetMapping("/findAll")
	public List<MemberEntity> findAll() {
		List<MemberEntity> list = testService.findAll();
		System.out.println("리스트 : " + list);
		return list;
	}

	@GetMapping("/deleteId")
	public String deleteId(@RequestParam("id") String id) {
		return testService.deleteId(id);
	}
	
	@GetMapping("/removeCache")
	public String cache(@RequestParam("id") String id) {
		redisCache.removeCaffeine(id);
		redisCache.removeJti(id);
		return "Removed cache for " + id;
	}
	
	@GetMapping("/lectureVideoOpen")
	public ResponseEntity<?> thisIsTestForVideoOpen(){
		Map<String , String> videoDTO = new HashMap<String, String>();
		videoDTO.put("videoId", "nNA-sbOzHl4");
		return ResponseEntity.ok(videoDTO);
	}
	
	@GetMapping("/lectureNoteOpenTest")
	public ResponseEntity<?> thisIsTestForlectureNoteOpen(@RequestParam(name = "page" , required = false , defaultValue = "0") String page){
		List<Map<String , Object>> lectureNoteDTOlist = new ArrayList<Map<String , Object>>();
		for(int i = 1 ; i < 4 ; i++) {
			Map<String , Object> lectureNoteDTO = new HashMap<String, Object>();
			lectureNoteDTO.put("lectureTitle", "노트를 작성한 강의 이름 : " + i);
			lectureNoteDTO.put("chapter", "강의의 섹션 이름 : " + i);
			lectureNoteDTO.put("subHeading", "소제목 : " + i);
			lectureNoteDTO.put("count", String.valueOf(i));
			
			List<Map<String , String>> lectureNoteDetailDTOList = new ArrayList<Map<String,String>>();
			for(int j = 1 ; j <= i ; j++) {
			Map<String , String> lectureNoteDetailDTO = new HashMap<String , String>();
			lectureNoteDetailDTO.put("noteId", "노트의 Id : " + i);
			lectureNoteDetailDTO.put("noteTitle", "노트의 제목 : " + i + "번 제목");
			lectureNoteDetailDTO.put("date", "2023-" + String.format("%02d" ,(int) ((Math.random() * 12) + 1)) + "-" + String.format("%02d" ,(int) ((Math.random() * 31) + 1)));
			lectureNoteDetailDTO.put("lastModifiedDate" , "2023-" + String.format("%02d" ,(int) ((Math.random() * 12) + 1)) + "-" + String.format("%02d" ,(int) ((Math.random() * 31) + 1)));
			lectureNoteDetailDTO.put("content", "<h1>This is a Heading 1</h1>"
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
			lectureNoteDetailDTOList.add(lectureNoteDetailDTO);
			}
			lectureNoteDTO.put("detail" , lectureNoteDetailDTOList);
			lectureNoteDTOlist.add(lectureNoteDTO);
		}
		return ResponseEntity.ok(lectureNoteDTOlist);
	}
	
	@GetMapping("/lectureQnAOpen")
	public ResponseEntity<?> thisIsTestForlectureQnAOpen(){
		List<Map<String , String>> lectureQnADTOlist = new ArrayList<Map<String , String>>();
		for(int i = 1 ; i < 11 ; i++) {			
			Map<String , String> lectureQnADTO = new HashMap<String, String>();
			lectureQnADTO.put("lectureTitle", "질문을 작성한 강의 이름 : " + i);
			lectureQnADTO.put("chapter", "강의의 섹션 이름 : " + i);
			lectureQnADTO.put("subHeading", "소제목 : " + i);
			lectureQnADTO.put("questionTitle", "질문의 제목 : " + i);
			lectureQnADTO.put("date", "2023-" + String.format("%02d" ,(int) ((Math.random() * 12) + 1)) + "-" + String.format("%02d" ,(int) ((Math.random() * 31) + 1)));
			lectureQnADTO.put("content", "<h1>This is a Heading 1</h1>"
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
			lectureQnADTOlist.add(lectureQnADTO);
		}
		return ResponseEntity.ok(lectureQnADTOlist);
	}

	@PostMapping("/testpost")
	public Object testpost(@RequestParam(name = "post") String post) {
		return post;
	}
	
	@PutMapping("/testput")
	public Object testput(@RequestParam(name = "post") String post) {
		return post;
	}
	
	@DeleteMapping("/testdelete")
	public Object testdelete(@RequestParam(name = "post") String post) {
		return post;
	}
}