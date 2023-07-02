package com.devrun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.entity.MemberEntity;
import com.devrun.service.SignupService;

@RestController
public class TestController {
	@Autowired
	SignupService signupService;
	
	@GetMapping("/tmi")
	public MemberEntity tmi(@RequestParam("id") String id) {
		System.out.println("컨트롤러");
        return signupService.findById(id);
	}
}
