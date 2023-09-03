package com.devrun.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.member.SignupDTO;
import com.devrun.dto.member.MemberDTO.Status;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.PointEntity;
import com.devrun.service.EmailSenderService;
import com.devrun.service.MemberService;
import com.devrun.util.CaffeineCache;

import reactor.core.publisher.Mono;

@RestController
public class SignupController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailSenderService emailSenderService;
	
	@Autowired
    private CaffeineCache caffeineCache;
	
	// ID 중복확인
	@PostMapping("/checkID")
	@ResponseBody
    public String checkID(@RequestBody SignupDTO signupDTO) {
		String id = signupDTO.getId();
        int result = memberService.checkID(id);
        return result + "";
    }
	
	// Email 중복확인
	@PostMapping("/checkEmail")
	@ResponseBody
    public String checkEmail(@RequestBody SignupDTO signupDTO) {
		String email = signupDTO.getEmail();
		int result = memberService.checkEmail(email);
        return result + "";
    }

	// Phone 중복확인
	@PostMapping("/checkPhone")
	@ResponseBody
	public String checkPhone(@RequestBody SignupDTO signupDTO) {
		String phonenumber = signupDTO.getPhonenumber();
		int result = memberService.checkphone(phonenumber);
		return result + "";
	}

	// 핸드폰 인증번호 전송
	@PostMapping("/auth/phone")
	@ResponseBody
	public Mono<String> authPhonenumber(@RequestBody SignupDTO signupDTO) {
		String phonenumber = signupDTO.getPhonenumber();
		System.out.println("폰" + phonenumber);
        return memberService.sendSmsCode(phonenumber);
    }
	
	// 핸드폰 인증번호 확인
	@ResponseBody
	@PostMapping("/verify/phone")
	public ResponseEntity<?> verify(@RequestBody SignupDTO signupDTO) {
		String phonenumber = signupDTO.getPhonenumber();
		String code = signupDTO.getCode();
        if (memberService.verifyCode(phonenumber, code)) {
        	// 200 인증성공
        	return new ResponseEntity<>("Verification successful", HttpStatus.OK);
        } else {
        	// 403 인증실패
        	return new ResponseEntity<>("Verification failed", HttpStatus.FORBIDDEN);
        }
    }
	
	// 회원가입
	@ResponseBody
	@PostMapping("/signup/okay")
	// @Transactional이 적용된 메소드나 클래스는 Spring에 의해 트랜잭션 경계가 설정되고, 
	// 해당 트랜잭션 내에서 실행되는 모든 데이터베이스 작업은 단일 트랜잭션으로 묶입니다. 
	// 이렇게 하면 데이터 무결성이 보장되며, 예외가 발생하면 트랜잭션이 롤백되어 데이터베이스 상태가 이전 상태로 복원됩니다.
	@Transactional																// code는 파라미터로													
	public ResponseEntity<?> okay(@RequestBody @Valid MemberEntity memberEntity, String code) {
		// @Valid 어노테이션이 있는 경우, Spring은 요청 본문을 MemberEntity 객체로 변환하기 전에 Bean Validation API를 사용하여 유효성 검사를 수행
		
		System.out.println(memberEntity);
		System.out.println(memberEntity.getPassword());
		System.out.println("생일 : " + memberEntity.getBirthday());
		
		System.out.println("트루가 맞냐" + memberEntity.isAgeConsent());
		System.out.println("아이디 유효성 검사 : " + memberService.validateId(memberEntity.getId()));
		System.out.println("이메일 유효성 검사 : " + memberService.validateEmail(memberEntity.getEmail()));
		System.out.println("비밀번호 유효성 검사" + memberService.validatePassword(memberEntity.getPassword()));
		// 회원정보 입력
		if (memberService.checkID(memberEntity.getId()) == 0 
				&& memberService.checkEmail(memberEntity.getEmail()) == 0
				&& memberService.checkphone(memberEntity.getPhonenumber()) == 0
//				&& memberService.verifyCode(memberEntity.getPhonenumber(), code)
				) {
//			// 403 약관 미동의    
//			if (!memberEntity.isAgeConsent() 
//			           || !memberEntity.isTermsOfService() 
//			           || !memberEntity.isPrivacyConsent()) {
//				return new ResponseEntity<>("User has not agreed to the terms", HttpStatus.FORBIDDEN);
//			}
			// 회원가입 성공
//			else 
				if (memberService.validateId(memberEntity.getId()) 
					&& memberService.validateEmail(memberEntity.getEmail()) 
					&& memberService.validatePassword(memberEntity.getPassword())
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

					LocalDate userBirthday = memberEntity.getBirthday();

					// 사용자의 생년월일로부터 19년 후의 날짜 계산
					LocalDate after19Years = userBirthday.plusYears(19);

					// 현재 날짜가 사용자의 생년월일로부터 19년 후의 날짜 이후라면 19세 이상
					// currentDate.isEqual(after19Years) : 생일 당일도 19세로 인정
					if (localCurrentDate.isAfter(after19Years) || localCurrentDate.isEqual(after19Years)) {
						
					    try {
					    	// 가입일자 설정
					    	
					    	// 원하는 시간대로 변경 가능
					    	ZoneId zoneId = ZoneId.systemDefault();
					    	
					    	// 현재 날짜와 시간을 LocalDateTime 형식으로 가져오기
					    	LocalDateTime localCurrentDateTime = LocalDateTime.now(zoneId);

					    	// LocalDateTime을 Instant로 변환하기
					    	Instant currentInstant = localCurrentDateTime
					    			// atZone() 메서드를 사용하여 시스템 기본 시간대를 사용
					    			.atZone(zoneId)
					    			// toInstant() 메서드를 사용하여 Instant로 변환
					    			.toInstant();

					    	// Instant를 java.util.Date 객체로 변환
					    	// 이를 위해 Date.from() 메서드를 사용
					    	Date currentDate = Date.from(currentInstant);

					    	// 가입일자를 현재 날짜와 시간으로 설정
					    	memberEntity.setSignupDate(currentDate);
							
					    	// 비밀번호 암호화
					        String encodedPassword = passwordEncoder.encode(memberEntity.getPassword());
					        memberEntity.setPassword(encodedPassword); // 암호화된 비밀번호 설정
					    	
					    	// 사용자 등록 시도
					    	MemberEntity m = memberService.insert(memberEntity);
					    	
					    	if (m != null) {
					    		
					    		// 회원가입 축하 포인트 지급
					    		PointEntity point = new PointEntity();
					    		point.setMypoint(3000);
					    		
					    		// PointEntity 객체에 MemberEntity 객체를 설정
						    	point.setMemberEntity(m);
						    	
						    	System.out.println("멤버"+m);
						    	System.out.println("포인트"+point);
						    	
						    	// PointEntity에 MemberEntity가 제대로 설정되었는지 검사
						        if (point.getUserNo() != -1) {
						        	
						        	// 포인트 정보 등록 시도 실패 시
						            if (memberService.insert(point) == null) {
						                return new ResponseEntity<>("Failed to save point", HttpStatus.INTERNAL_SERVER_ERROR);
						            }
						            
					            // MemberEntity 설정 실패 시
						        } else {
						            return new ResponseEntity<>("Failed to set point entity", HttpStatus.INTERNAL_SERVER_ERROR);
						        }
						        
					        // 사용자 등록 실패 시
						    } else {
						        return new ResponseEntity<>("Failed to register the user", HttpStatus.INTERNAL_SERVER_ERROR);
						    }
					    	
				    	// 기타 데이터베이스 오류 발생 시
						} catch (Exception e) {
							System.out.println("Error: " + e.getMessage());
						    return new ResponseEntity<>("Failed to register for database", HttpStatus.INTERNAL_SERVER_ERROR);
						}
					    
					    // 회원가입 인증 메일 발송
					    emailSenderService.sendSignupByEmail(memberEntity.getEmail(), memberEntity.getId());
					    // 메모리에 저장된 전화번호와 인증코드 제거
					    memberService.removeVerifyCode(memberEntity.getPhonenumber());
					    return new ResponseEntity<>("Signup successful", HttpStatus.OK);
					    //ResponseEntity.ok("Signup successful");
					    
				    // 400 19세 미만
					} else {
					    return new ResponseEntity<>("User is under 19 years old", HttpStatus.BAD_REQUEST);
					}
					
			// 400 회원가입 실패 (잘못된 입력 데이터)
			} else {
				System.out.println("유효하지 않은 데이터");
				return new ResponseEntity<>("Invalid input data", HttpStatus.BAD_REQUEST);
						//ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input data");				response.data에 담겨있던 것을 response.data.massage로 변경
			}
			
		// 409 중복된 아이디
		} else if(memberService.checkID(memberEntity.getId()) != 0) {
		    return new ResponseEntity<>("UserId already taken", HttpStatus.CONFLICT);
		    		//ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UserId already taken");
		
		// 409 중복된 이메일
		} else if(memberService.checkEmail(memberEntity.getEmail()) != 0) {
		    return new ResponseEntity<>("Email already registered", HttpStatus.CONFLICT);
		    		//ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email already registered");
		
		// 409 중복된 핸드폰번호
		} else if(memberService.checkphone(memberEntity.getPhonenumber()) != 0) {
		    return new ResponseEntity<>("Phone number already registered", HttpStatus.CONFLICT);
		    		//ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Phone number already registered");
		
		// 403 인증되지 않은 전화번호
		} else if(!memberService.verifyCode(memberEntity.getPhonenumber(), code)) {
			
			return new ResponseEntity<>("Verification failed Phonenumber", HttpStatus.FORBIDDEN);
		// 기타 오류 500
		} else {
		    return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
		    		//ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
		}

	}
	
	// 회원가입 인증 이메일 재발송
	@ResponseBody
	@PostMapping("/signup/resend/confirm-email")
	public ResponseEntity<?> signupEmail(@RequestParam("email") String toEmail
										, @RequestParam("id") String id) {
		
        try {
        	emailSenderService.sendSignupByEmail(toEmail, id);
        	// 200 성공
        	return ResponseEntity.status(200).body("Email sent successfully");
        } catch (Exception e) {
        	System.out.println("이메일 에러 : " + e);
        	// 403 이메일 전송 실패
        	return ResponseEntity.status(403).body("Failed to send email");
        }
	}
	
	// 회원가입 인증 확인
	@ResponseBody
	@Transactional
	@PostMapping("/verify/signupEmail")
	public ResponseEntity<?> signupOk(@RequestParam("id") String id
										, @RequestParam("key") String key){
		
		MemberEntity member = memberService.findById(id);
		
		if (member != null) {
			// 현재 시간
			Instant currentDate = Instant.now();
			// 회원가입 시간
			Instant signupDate = member.getSignupDate().toInstant();
			long diffInMinutes = Duration.between(signupDate, currentDate).toMinutes();
			
			System.out.println("현재시간 : " + currentDate);
			System.out.println("가입시간 : " + signupDate);
			System.out.println("시간 차이 : " + diffInMinutes);
			
			if (diffInMinutes < 60) {
				
				String cachedKey = caffeineCache.getCaffeine(id);
				if (cachedKey != null && cachedKey.equals(key)) {
					
					member.setStatus(Status.ACTIVE);
					
					memberService.insert(member);
					caffeineCache.removeCaffeine(id);
					
					// 이메일 인증 성공 회원 활성화
					return ResponseEntity.status(200).body("Email verification successful, account activated");
				} else {
					// 유효하지 않은 키
					return ResponseEntity.status(400).body("Invalid key");
				}
				
			} else {
				// 회원가입 1시간 경과
				return ResponseEntity.status(400).body("Verification expired");
			}
			
		} else {
			// 회원을 찾을 수 없음
			return ResponseEntity.status(404).body("Member not found");
		}
	}
	
}