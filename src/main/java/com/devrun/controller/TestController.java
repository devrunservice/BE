package com.devrun.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.entity.MemberEntity;
import com.devrun.service.SignupService;
import com.devrun.service.TestService;

@RestController
public class TestController {
	
	@Autowired
	SignupService signupService;
	
	@Autowired
	TestService testService;
//	122.41.29.73
//	@CrossOrigin(origins = "localhost:3000" , allowedHeaders = {"GET"})
	@GetMapping("/tmi")
	public ResponseEntity<?> tmi(@RequestParam("id") String id) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String userId = authentication.getName();
	    if (userId.equals(id)) {
	        MemberEntity member = signupService.findById(id);
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