package com.devrun.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.member.SignupDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.service.SignupService;

import reactor.core.publisher.Mono;

@RestController
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

	@PostMapping("/auth/phone")
	@ResponseBody
	public Mono<String> authPhonenumber(@RequestBody SignupDTO signupDTO) {
		String phonenumber = signupDTO.getPhonenumber();
		System.out.println("폰" + phonenumber);
        return signupService.sendSmsCode(phonenumber);
    }
	
	@ResponseBody
	@PostMapping("/verify")
	 public ResponseEntity<?> verify(@RequestBody SignupDTO signupDTO) {
		String phonenumber = signupDTO.getPhonenumber();
		String code = signupDTO.getCode();
        if (signupService.verifySmsCode(phonenumber, code)) {
        	// 200 인증성공
        	return new ResponseEntity<>("Verification successful", HttpStatus.OK);
        } else {
        	// 403 인증실패
        	return new ResponseEntity<>("Verification failed", HttpStatus.FORBIDDEN);
        }
    }
	
	@ResponseBody
	@PostMapping("/signup/okay")												// code는 파라미터로
	public ResponseEntity<?> okay(@RequestBody @Valid MemberEntity memberEntity, String code) {
		// @Valid 어노테이션이 있는 경우, Spring은 요청 본문을 MemberEntity 객체로 변환하기 전에 Bean Validation API를 사용하여 유효성 검사를 수행
		System.out.println(memberEntity);
		System.out.println(memberEntity.getEmail());

		System.out.println("트루가 맞냐" + memberEntity.isAgeConsent());
		System.out.println("아이디 유효성 검사 : " + signupService.validateId(memberEntity.getId()));
		System.out.println("이메일 유효성 검사 : " + signupService.validateEmail(memberEntity.getEmail()));
		System.out.println("비밀번호 유효성 검사" + signupService.validatePassword(memberEntity.getPassword()));
		// 회원정보 입력
		if (signupService.checkID(memberEntity.getId()) == 0 
				&& signupService.checkEmail(memberEntity.getEmail()) == 0
				&& signupService.checkphone(memberEntity.getPhonenumber()) == 0
				&& signupService.verifySmsCode(memberEntity.getPhonenumber(), code)
				) {
//			// 403 약관 미동의    
//			if (!memberEntity.isAgeConsent() 
//			           || !memberEntity.isTermsOfService() 
//			           || !memberEntity.isPrivacyConsent()) {
//				return new ResponseEntity<>("User has not agreed to the terms", HttpStatus.FORBIDDEN);
//			}
			// 회원가입 성공
//			else 
				if (signupService.validateId(memberEntity.getId()) 
					&& signupService.validateEmail(memberEntity.getEmail()) 
					&& signupService.validatePassword(memberEntity.getPassword())
					) {
				System.out.println("회원가입 성공");
				
				// 가입일자 저장
				Date currentDate = new Date();
				memberEntity.setSignup(currentDate);
		    	signupService.insert(memberEntity);
		    	
		    	// 메모리에 저장된 전화번호와 인증코드 제거
				signupService.removeSmsCode(memberEntity.getPhonenumber());
				
				return new ResponseEntity<>("Signup successful", HttpStatus.OK);
						//ResponseEntity.ok("Signup successful");
				
			// 회원가입 실패 (잘못된 입력 데이터) 400
			} else {
				System.out.println("유효하지 않은 데이터");
				return new ResponseEntity<>("Invalid input data", HttpStatus.BAD_REQUEST);
						//ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input data");				response.data에 담겨있던 것을 response.data.massage로 변경
			}
			
		// 409 중복된 아이디
		} else if(signupService.checkID(memberEntity.getId()) != 0) {
		    return new ResponseEntity<>("UserId already taken", HttpStatus.CONFLICT);
		    		//ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UserId already taken");
		
		// 409 중복된 이메일
		} else if(signupService.checkEmail(memberEntity.getEmail()) != 0) {
		    return new ResponseEntity<>("Email already registered", HttpStatus.CONFLICT);
		    		//ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email already registered");
		
		// 409 중복된 핸드폰번호
		} else if(signupService.checkphone(memberEntity.getPhonenumber()) != 0) {
		    return new ResponseEntity<>("Phone number already registered", HttpStatus.CONFLICT);
		    		//ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Phone number already registered");
		
		// 403 인증되지 않은 전화번호
		} else if(!signupService.verifySmsCode(memberEntity.getPhonenumber(), code)) {
			
			return new ResponseEntity<>("Verification failed Phonenumber", HttpStatus.FORBIDDEN);
		// 기타 오류 500
		} else {
		    return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
		    		//ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
		}

	}
	
}