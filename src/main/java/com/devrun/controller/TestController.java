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
		String id = memberService.getIdFromToken(request);
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