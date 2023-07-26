package com.devrun.controller;

import java.time.LocalDate;
import java.time.ZoneId;
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
					
					//LocalDate와 Date는 둘 다 Java에서 날짜를 표현하는 클래스입니다. 그러나 사용 방식과 기능에는 몇 가지 중요한 차이점이 있습니다.
					//1. Java 버전: Date 클래스는 Java의 초기 버전부터 존재했습니다. 
					//	 그러나 이 클래스는 여러 가지 문제점이 있어서 Java 8에서 새로운 날짜/시간 API가 도입되었고, 그 중 하나가 LocalDate입니다.
					//2. 불변성: Date 객체는 변경 가능(mutable)합니다. 즉, 일단 생성된 Date 객체의 상태를 변경할 수 있습니다. 반면에 LocalDate는 불변(immutable)입니다.
					//	 즉, 일단 생성된 LocalDate 객체는 변경할 수 없으며, 모든 조작은 새로운 LocalDate 객체를 생성하여 반환합니다. 
					//	 이는 멀티스레드 환경에서의 안정성을 높여줍니다.
					//3. 시간대: Date는 시간대를 갖고 있습니다. Date 객체는 1970년 1월 1일 00:00:00 UTC(협정 세계시)부터의 밀리초로 표현됩니다. 
					//	 반면에 LocalDate는 시간대를 갖고 있지 않습니다. 
					//	 LocalDate는 날짜만을 표현하며, 시간대나 시간 정보는 포함하지 않습니다.
					//4. 사용 편의성: LocalDate는 Date에 비해 사용하기가 더 편리합니다. 예를 들어, LocalDate는 날짜를 더하거나 빼는 등의 연산을 위한 메서드를 제공합니다.
					//	 또한, LocalDate를 사용하면 ISO 8601 날짜 표준에 따라 날짜를 쉽게 파싱하고 출력할 수 있습니다.
					//5. 정확성: Date 클래스는 월을 0부터 시작하여 표현하기 때문에 혼란을 줄 수 있습니다. 
					//	 예를 들어, 1월은 0, 2월은 1로 표현됩니다. 반면, LocalDate는 이러한 문제를 해결하고 월을 1부터 시작하여 표현합니다.
					//따라서 가능하다면 LocalDate나 LocalDateTime 같은 Java 8의 새로운 날짜/시간 API를 사용하는 것이 좋습니다. 
					//이는 보다 안정적이고, 사용하기 쉽고, 여러 가지 추가 기능을 제공합니다.
					
					// 현재 날짜 구하기
					LocalDate localCurrentDate = LocalDate.now();

					// 사용자의 생년월일을 LocalDate 형식으로 변환
					// .toInstant(): java.util.Date 객체를 java.time.Instant 객체로 변환합니다. Instant는 1970년 1월 1일 00:00:00 UTC를 기점으로 한 특정 시점을 나노초 단위로 표현합니다.
					LocalDate userBirthday = memberEntity.getBirthday().toInstant()
						  // ZoneId.systemDefault()를 사용하여 시스템 기본 시간대를 사용
					      .atZone(ZoneId.systemDefault())
					      // ZonedDateTime이나 Instant를 LocalDate로 변환
					      .toLocalDate();

					// 사용자의 생년월일로부터 19년 후의 날짜 계산
					LocalDate after19Years = userBirthday.plusYears(19);

					// 현재 날짜가 사용자의 생년월일로부터 19년 후의 날짜 이후라면 19세 이상
					// currentDate.isEqual(after19Years) : 생일 당일도 19세로 인정
					if (localCurrentDate.isAfter(after19Years) || localCurrentDate.isEqual(after19Years)) {
					    // 회원가입 처리
						System.out.println("회원가입 성공");
						
						// 가입일자 저장
						Date currentDate = Date.from(localCurrentDate
								// .atStartOfDay(): LocalDate 객체에서 해당 날짜의 시작 시간(00:00:00)을 나타내는 LocalDateTime 객체를 생성
								.atStartOfDay(ZoneId.systemDefault()).toInstant());
						// 이러한 메서드들을 연결하여 사용함으로써, Date에서 LocalDate로 변환하거나, LocalDate에서 Date로 변환하는 등의 작업을 수행할 수 있습니다.
						// 이는 Java 8에서 도입된 새로운 날짜/시간 API와 이전의 Date 클래스 사이의 호환성을 유지하기 위해 필요한 작업입니다.
						
					    memberEntity.setSignup(currentDate);
						signupService.insert(memberEntity);
						
						// 메모리에 저장된 전화번호와 인증코드 제거
						signupService.removeSmsCode(memberEntity.getPhonenumber());
						
						return new ResponseEntity<>("Signup successful", HttpStatus.OK);
						//ResponseEntity.ok("Signup successful");
					} else {
						// 400 19세 미만
					    return new ResponseEntity<>("User is under 19 years old", HttpStatus.BAD_REQUEST);
					}
					
			// 400 회원가입 실패 (잘못된 입력 데이터)
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