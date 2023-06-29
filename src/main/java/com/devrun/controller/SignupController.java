package com.devrun.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devrun.dto.member.SignupDTO;
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
    public String checkID(@RequestBody SignupDTO signupDTO) {
		String id = signupDTO.getId();
        int result = signupService.checkID(id);
        return result + "";
    }
	
	@PostMapping("/checkEmail")
	@ResponseBody
    public String checkEmail(@RequestBody SignupDTO signupDTO) {
		String email = signupDTO.getEmail();
		int result = signupService.checkEmail(email);
        return result + "";
    }

	@PostMapping("/checkPhone")
	@ResponseBody
	public String checkPhone(@RequestBody SignupDTO signupDTO) {
		String phonenumber = signupDTO.getPhonenumber();
		int result = signupService.checkphone(phonenumber);
		return result + "";
	}

	@PostMapping("/signup/auth")
	@ResponseBody
	public Mono<String> auth(@RequestBody SignupDTO signupDTO) {
		String phonenumber = signupDTO.getPhonenumber();
		System.out.println("폰" + phonenumber);
        return signupService.sendSmsCode(phonenumber);
    }
	
	@ResponseBody
	@PostMapping("/verify")
	 public ResponseEntity<?> verify(@RequestBody SignupDTO signupDTO) {
		String phonenumber = signupDTO.getPhonenumber();
		int code = signupDTO.getCode();
        if (signupService.verifySmsCode(phonenumber, code)) {
        	// 200 인증성공
        	// ResponseEntity.ok()는 HTTP 상태 코드 200을 의미
            return ResponseEntity.ok("Verification successful");
        } else {
        	// 401 인증실패
        	// HttpStatus.UNAUTHORIZED는 HTTP 상태 코드 401을 의미
        	// .body("Verification failed")는 응답 본문에 "Verification failed"라는 텍스트 메시지를 추가
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Verification failed");
        }
    }
	
	@ResponseBody
	@PostMapping("/signup/okay")
	public ResponseEntity<?> okay(@RequestBody MemberEntity memberEntity) {
		// @ModelAttribute 어노테이션이 붙은 매개변수를 HTTP 요청 파라미터와 자동으로 바인딩하도록 지원합니다.
		// 이 기능은 HTML form 태그의 input 필드와 Java 객체의 필드를 매핑하여 사용하게 해줍니다.
		System.out.println(memberEntity);
		System.out.println(memberEntity.getEmail());
		
		// 가입일자 저장
		Date currentDate = new Date();
		memberEntity.setSignup(currentDate);
		System.out.println("아이디 유효성 검사 : " + signupService.validateId(memberEntity.getId()));
		System.out.println("이메일 유효성 검사 : " + signupService.validateEmail(memberEntity.getEmail()));
		System.out.println("비밀번호 유효성 검사" + signupService.validatePassword(memberEntity.getPassword()));
		// 회원정보 입력
		if (signupService.checkID(memberEntity.getId()) == 0 
				&& signupService.checkEmail(memberEntity.getEmail()) == 0
//				&& signupService.checkphone(memberEntity.getPhonenumber()) == 0					테스트 기간에만 중복확인 x
				) {
			
			// 회원가입 성공
			if (signupService.validateId(memberEntity.getId()) 
					&& signupService.validateEmail(memberEntity.getEmail()) 
					&& signupService.validatePassword(memberEntity.getPassword())
					) {
				System.out.println("회원가입 성공");
				signupService.insert(memberEntity);
				return ResponseEntity.ok("Signup successful");
				
			// 회원가입 실패 (잘못된 입력 데이터) 400
			} else {
				System.out.println("유효하지 않은 데이터");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input data");
			}
			
		// 중복된 아이디
		} else if(signupService.checkID(memberEntity.getId()) != 0) {
		    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UserId already taken");
		
		// 중복된 이메일
		} else if(signupService.checkEmail(memberEntity.getEmail()) != 0) {
		    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email already registered");
		
		// 중복된 핸드폰번호
		} else if(signupService.checkphone(memberEntity.getPhonenumber()) != 0) {
		    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Phone number already registered");
		    
		// 기타 오류 500
		} else {
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
		}

	}
	
}