package com.devrun.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
	@CrossOrigin(origins = "localhost:3000" , allowedHeaders = {"GET"})
	@GetMapping("/tmi")
	public MemberEntity tmi(@RequestParam("id") String id) {
		
        return signupService.findById(id);
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
