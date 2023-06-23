package com.devrun.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devrun.entity.MemberEntity;
import com.devrun.service.SignupService;

import reactor.core.publisher.Mono;

@Controller
public class SignupController {
	
	@Autowired
	SignupService signupService;
	
	@ResponseBody
	@PostMapping("/signup")
	public String signup(HttpServletResponse response) {
		return "signup";
	}

	@PostMapping("/checkID")
	@ResponseBody
    public String checkID(@RequestParam("id") String id) {
        int result = signupService.checkID(id);
        return result + "";
    }
	
	@PostMapping("/checkEmail")
	@ResponseBody
    public String checkEmail(@RequestParam("email") String email) {
        int result = signupService.checkEmail(email);
        return result + "";
    }

	@PostMapping("/checkPhone")
	@ResponseBody
	public String checkPhone(@RequestParam("phone") String phonenumber) {
		int result = signupService.checkphone(phonenumber);
		return result + "";
	}
	
	@ResponseBody
	@PostMapping("/signup/auth")
	public Mono<String> auth(@RequestParam("phonenumber") String phone) {
		System.out.println("폰" + phone);
        return signupService.sendSmsCode(phone);
    }
	
	@ResponseBody
	@PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam("phonenumber") String phone, @RequestParam("code") int code) {
        if (signupService.verifySmsCode(phone, code)) {
        	// 200 인증성공
            return ResponseEntity.ok("Verification successful");
        } else {
        	// 401 인증실패
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Verification failed");
        }
    }
	
	@ResponseBody
	@PostMapping("/signup/okay")
	public String okay(@RequestBody MemberEntity memberEntity) {
		// @ModelAttribute 어노테이션이 붙은 매개변수를 HTTP 요청 파라미터와 자동으로 바인딩하도록 지원합니다.
		// 이 기능은 HTML form 태그의 input 필드와 Java 객체의 필드를 매핑하여 사용하게 해줍니다.
		System.out.println(memberEntity);
		System.out.println(memberEntity.getEmail());
		
		Date currentDate = new Date();
		memberEntity.setSignup(currentDate);
		memberEntity.setLastlogin(currentDate);
		
		signupService.insert(memberEntity);
		
		return "okay";
	}
	
	
	
}
