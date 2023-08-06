package com.devrun.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.entity.MemberEntity;
import com.devrun.service.MemberService;
import com.devrun.service.TestService;
import com.devrun.util.JWTUtil;

@RestController
public class TestController {
	
	@Autowired
	MemberService signupService;
	
	@Autowired
	TestService testService;
	
	@Autowired
	MemberService memberService;
	
//	122.41.29.73
//	@CrossOrigin(origins = "localhost:3000" , allowedHeaders = {"GET"})
	@GetMapping("/tmi")
	public ResponseEntity<?> tmi(HttpServletRequest request) {
		// refreshToken이 헤더에 있는지 확인
	    String accessToken = request.getHeader("Access_token");

//	    // Refresh Token 존재 여부 확인 (null 혹은 빈문자열 인지 확인)
	    if (accessToken == null || accessToken.isEmpty()) {
	    	// 400 : Access token 없음
	        return new ResponseEntity<>("Access token is required", HttpStatus.BAD_REQUEST);
	    }
	    
		String id = JWTUtil.getUserIdFromToken(accessToken);
	    if (memberService.isUserIdEquals(id)) {
	        MemberEntity member = memberService.findById(id);
	        return ResponseEntity.ok(member);
	    } else {
	    	// 401 토큰의 사용자와 요청한 사용자 불일치
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request");
	    }
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
	
}