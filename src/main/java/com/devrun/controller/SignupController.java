package com.devrun.controller;

import java.lang.reflect.Type;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.member.MemberDTO.Status;
import com.devrun.dto.member.SignupDTO;
import com.devrun.dto.member.SignupWrapper;
import com.devrun.entity.Consent;
import com.devrun.entity.Contact;
import com.devrun.entity.LoginInfo;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.PointEntity;
import com.devrun.entity.PointHistoryEntity;
import com.devrun.repository.PointHistoryRepository;
import com.devrun.service.EmailSenderService;
import com.devrun.service.MemberService;
import com.devrun.util.AESUtil;
import com.devrun.util.CaffeineCache;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
	
	@Autowired
	private PointHistoryRepository pointHistoryRepository;
	
	// ID 중복확인
	@ResponseBody
	@PostMapping("/checkID")
	@ApiOperation(value = "ID 중복 확인", notes = "입력한 ID가 중복되는지 확인합니다.")
	@ApiImplicitParam(name = "id", value = "확인할 아이디", required = true, paramType = "body", dataType = "string")
    public String checkID(@RequestBody SignupDTO signupDTO) {
		String id = signupDTO.getId();
        int result = memberService.checkID(id);
        return result + "";
    }
	
	// Email 중복확인
	@ResponseBody
	@PostMapping("/checkEmail")
	@ApiOperation(value = "이메일 중복 확인", notes = "입력한 이메일이 중복되는지 확인합니다.")
	@ApiImplicitParam(name = "email", value = "확인할 이메일", required = true, paramType = "body", dataType = "string")
    public String checkEmail(@RequestBody SignupDTO signupDTO) {
		String email = signupDTO.getEmail();
		int result = memberService.checkEmail(email);
        return result + "";
    }

	// Phone 중복확인
	@ResponseBody
	@PostMapping("/checkPhone")
	@ApiOperation(value = "핸드폰 번호 중복 확인", notes = "입력한 핸드폰 번호가 중복되는지 확인합니다.")
	@ApiImplicitParam(name = "phonenumber", value = "확인할 핸드폰 번호", required = true, paramType = "body", dataType = "string")
	public String checkPhone(@RequestBody SignupDTO signupDTO) {
		String phonenumber = signupDTO.getPhonenumber();
		int result = memberService.checkphone(phonenumber);
		return result + "";
	}

	// 핸드폰 인증번호 전송
	@ResponseBody
	@PostMapping("/auth/phone")
	@ApiOperation(value = "핸드폰 인증번호 전송", notes = "핸드폰으로 인증번호를 전송합니다.")
	@ApiImplicitParam(name = "phonenumber", value = "전송할 핸드폰 번호", required = true, paramType = "body", dataType = "string")
	public Mono<String> authPhonenumber(@RequestBody SignupDTO signupDTO) {
		String phonenumber = signupDTO.getPhonenumber();
		System.out.println("폰" + phonenumber);
        return memberService.sendSmsCode(phonenumber);
    }
	
	// 핸드폰 인증번호 확인
	@ResponseBody
	@PostMapping("/verify/phone")
	@ApiOperation(value = "핸드폰 인증번호 확인", notes = "입력한 인증번호가 맞는지 확인합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "phonenumber", value = "확인할 핸드폰 번호", required = true, paramType = "body", dataType = "string"),
		@ApiImplicitParam(name = "code", value = "확인할 인증코드", required = true, paramType = "body", dataType = "string"),
		
		})
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "인증 성공"),
	    @ApiResponse(code = 403, message = "인증 실패")
	})
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

	@ResponseBody
	@PostMapping("/signup/okay")
	@Transactional
	@ApiOperation(value = "회원가입 처리", notes = "입력한 정보로 회원가입을 진행합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "아이디", required = true, paramType = "body", dataType = "string"),
		@ApiImplicitParam(name = "password", value = "비밀번호", required = true, paramType = "body", dataType = "string"),
		@ApiImplicitParam(name = "birthday", value = "생일", required = true, paramType = "body", dataType = "string"),
		@ApiImplicitParam(name = "name", value = "이름", required = true, paramType = "body", dataType = "string"),
		@ApiImplicitParam(name = "email", value = "이메일", required = true, paramType = "body", dataType = "string"),
		@ApiImplicitParam(name = "phonenumber", value = "핸드폰 번호", required = true, paramType = "body", dataType = "string"),
		@ApiImplicitParam(name = "code", value = "핸드폰 인증코드", required = true, paramType = "body", dataType = "string"),
		@ApiImplicitParam(name = "ageConsent", value = "나이 약관동의", required = true, paramType = "body", dataType = "string"),
		@ApiImplicitParam(name = "termsOfService", value = "서비스 약관동의", required = true, paramType = "body", dataType = "string"),
		@ApiImplicitParam(name = "privacyConsent", value = "개인정보 수집 약관동의", required = true, paramType = "body", dataType = "string"),
		@ApiImplicitParam(name = "marketConsent", value = "마케팅 활용 약관동의", required = true, paramType = "body", dataType = "string"),
	})
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "회원가입 성공"),
	    @ApiResponse(code = 400, message = "잘못된 입력값"),
	    @ApiResponse(code = 409, message = "중복된 데이터"),
	    @ApiResponse(code = 500, message = "내부 서버 오류")
	})
	public ResponseEntity<?> okay(@RequestBody @Valid SignupWrapper signupWrapper) {
		
	    MemberEntity memberEntity = signupWrapper.getMemberEntity();
	    if (memberEntity == null) {
	        return new ResponseEntity<>("MemberEntity should not be null", HttpStatus.BAD_REQUEST);
	    }
	    // 유효성 검증
	    if (!validateInputData(memberEntity, signupWrapper.getContact(), signupWrapper.getCode())) {
	        return new ResponseEntity<>("Invalid input data", HttpStatus.BAD_REQUEST);
	    }
	    // 중복확인
	    if (isDuplicate(memberEntity, signupWrapper.getContact())) {
	        return new ResponseEntity<>("Duplicate data", HttpStatus.CONFLICT);
	    }

	    try {
	        // 데이터 저장
	        MemberEntity savedMember = saveMemberEntities(signupWrapper);
	        if (savedMember == null) {
	            return new ResponseEntity<>("Failed to register the user", HttpStatus.INTERNAL_SERVER_ERROR);
	        }

	        // 포인트 저장
	        if (!savePoints(memberEntity)) {
	            return new ResponseEntity<>("Failed to save points", HttpStatus.INTERNAL_SERVER_ERROR);
	        }

	        // 캐시 제거
	        memberService.removeVerifyCode(signupWrapper.getContact().getPhonenumber());

	        // 이메일 전송
	        sendEmail(signupWrapper.getContact().getEmail(), memberEntity.getId());

	        return new ResponseEntity<>("Signup successful", HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	// 회원정보 유효성 검증
	private boolean validateInputData(MemberEntity memberEntity, Contact contact, String code) {
	    return memberService.validateId(memberEntity.getId()) &&
	           memberService.validateEmail(contact.getEmail()) &&
	           memberService.validatePassword(memberEntity.getPassword()) &&
	           memberService.verifyCode(contact.getPhonenumber(), code);
	}

	// 회원정보 중복확인
	private boolean isDuplicate(MemberEntity memberEntity, Contact contact) {
	    return memberService.checkID(memberEntity.getId()) != 0 ||
	           memberService.checkEmail(contact.getEmail()) != 0 ||
	           memberService.checkphone(contact.getPhonenumber()) != 0;
	}

	// 회원정보 DB에 저장
	private MemberEntity saveMemberEntities(SignupWrapper signupWrapper) throws Exception {
	    MemberEntity memberEntity = signupWrapper.getMemberEntity();
	    Contact contact = signupWrapper.getContact();
	    Consent consent = signupWrapper.getConsent();
	    LoginInfo loginInfo = signupWrapper.getLoginInfo();

	    // 연관된 엔터티 설정
	    contact.setMemberEntity(memberEntity);
	    consent.setMemberEntity(memberEntity);
	    loginInfo.setMemberEntity(memberEntity);

	    String encodedPassword = passwordEncoder.encode(memberEntity.getPassword());
	    memberEntity.setPassword(encodedPassword);

	    MemberEntity savedMember = memberService.insert(memberEntity);
	    if (savedMember != null
	    	&& memberService.insert(contact) != null
	    	&& memberService.insert(consent) != null
	    	&& memberService.insert(loginInfo) != null
	        ) {
	        return savedMember;
	    }

	    return null;
	}

	// 회원가입 축하 포인트 저장
	private boolean savePoints(MemberEntity memberEntity) {
		//날짜 세팅
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm:ss", new Locale("ko"));
		String signupDate= dateTime.format(formatter);
		
		// 포인트 적립
	    PointEntity point = new PointEntity();
	    point.setMypoint(3000);
	    point.setMemberEntity(memberEntity);
	    
	    // 포인트 히스토리 
	    PointHistoryEntity historyEntityGain = new PointHistoryEntity();
	    historyEntityGain.setMemberEntity(memberEntity);
	    historyEntityGain.setUpdatetime(signupDate); 
	    historyEntityGain.setPointupdown(3000);
	    String gainname = "회원가입 축하";
	    historyEntityGain.setProductname(gainname);
	    String gainExplanation = "회원가입시 얻은 포인트";
	    historyEntityGain.setExplanation(gainExplanation);
	    pointHistoryRepository.save(historyEntityGain);
	    return memberService.insert(point) != null;
	}
	
	// 회원가입 인증 이메일 재발송
	@ResponseBody
	@PostMapping("/signup/resend/confirm-email")
	@ApiOperation(value = "이메일 재전송", notes = "회원가입 인증 이메일을 재전송합니다.")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "data", value = "암호화된 데이터", required = false, paramType = "query", dataType = "string"),
	    @ApiImplicitParam(name = "email", value = "이메일 주소", required = false, paramType = "query", dataType = "string"),
	    @ApiImplicitParam(name = "id", value = "사용자 아이디", required = false, paramType = "query", dataType = "string")
	})
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "이메일 성공적으로 전송"),
	    @ApiResponse(code = 400, message = "잘못된 요청"),
	    @ApiResponse(code = 403, message = "이메일 전송 실패"),
	    @ApiResponse(code = 500, message = "내부 서버 오류")
	})
	public ResponseEntity<?> signupEmail(@RequestParam(required = false) String data
										, @RequestParam(required = false) String email
										, @RequestParam(required = false) String id
										) {
		if (data != null && email == null && id == null) {
			try {
				System.out.println("encryptedData :" + data);
				// 암호화된 데이터를 복호화
				String decryptedData = AESUtil.decrypt(data);
				System.out.println("decryptedData : " + decryptedData);
				// 복호화된 데이터를 JSON 형식으로 파싱
				Type type = new TypeToken<HashMap<String, String>>() {}.getType();
				HashMap<String, String> map = new Gson().fromJson(decryptedData, type);
				
				// id, email, key 값 추출
				id = map.get("id");
				String toEmail = map.get("email");
				
				return sendEmail(toEmail, id);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(500).body(e.getMessage());
			}
		} else if (data == null && email != null && id != null) {
			return sendEmail(email, id);
		} else {
			// 잘못된 파라미터
		    return ResponseEntity.status(400).body("Bad Request");
		}
	}
	
	private ResponseEntity<?> sendEmail(String toEmail, String id) {
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
	@CrossOrigin(origins = {"https://mail.naver.com", "https://mail.daum.net", "https://mail.google.com","https://mail.nate.com"})
	@PostMapping("/verify/signupEmail")
	@ApiOperation(value = "이메일 인증 확인", notes = "이메일로부터 인증을 확인합니다.")
	@ApiImplicitParam(name = "data", value = "암호화된 데이터", required = true, paramType = "query", dataType = "string")
	@ApiResponses(value = {
	    @ApiResponse(code = 302, message = "다른 위치로 리다이렉션"),
	    @ApiResponse(code = 500, message = "내부 서버 오류")
	})
	public ResponseEntity<?> signupOk(@RequestParam("data") String encryptedData){
		HttpHeaders headers = new HttpHeaders();
		try {
			System.out.println("encryptedData :" + encryptedData);
	        // 암호화된 데이터를 복호화
	        String decryptedData = AESUtil.decrypt(encryptedData);
	        System.out.println("decryptedData : " + decryptedData);
	        // 복호화된 데이터를 JSON 형식으로 파싱
	        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
	        HashMap<String, String> map = new Gson().fromJson(decryptedData, type);

	        // id, email, key 값 추출
	        String id = map.get("id");
	        String key = map.get("key");
			MemberEntity member = memberService.findById(id);
	
			if (member == null) {
				// 302 : 회원을 찾을 수 없음
				headers.setLocation(URI.create("https://devrun.net/signupcompletion?status=notfound&data=" + encryptedData));
		        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
		    } else if(member.getStatus() == Status.ACTIVE) {
		    	// 이미 활성화된 유저
		    	headers.setLocation(URI.create("https://devrun.net/signupcompletion?status=activated"));
		    	return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
		    }
			
			if (isVerificationExpired(member.getSignupDate())) {
				// 회원가입 1시간 경과
				headers.setLocation(URI.create("https://devrun.net/signupcompletion?status=expired&data=" + encryptedData));
		        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
		    }
		    return verifyKeyAndActivateAccount(id, key, member, encryptedData);
		} catch (Exception e) {
			// 암호화 실패
			return new ResponseEntity<>("Decryption failed" + "\nencryptedData :" + encryptedData + "\nerror : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean isVerificationExpired(Date signupDate) {
		// 현재 시간
	    Instant currentDate = Instant.now();
	    // 회원가입 시간
	    Instant memberSignupDate = signupDate.toInstant();
	    long diffInMinutes = Duration.between(memberSignupDate, currentDate).toMinutes();
	    return diffInMinutes >= 60;
	}

	private ResponseEntity<?> verifyKeyAndActivateAccount(String id, String key, MemberEntity member, String encryptedData) {
		HttpHeaders headers = new HttpHeaders();
	    String cachedKey = caffeineCache.getCaffeine(id);
	    if (cachedKey != null && cachedKey.equals(key)) {
	        member.setStatus(Status.ACTIVE);
	        memberService.insert(member);
	        caffeineCache.removeCaffeine(id);
	        // 이메일 인증 성공 회원 활성화
	        headers.setLocation(URI.create("https://devrun.net/signupcompletion?status=success"));
	        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
	    }
	    // 유효하지 않은 키
	    headers.setLocation(URI.create("https://devrun.net/signupcompletion?status=failure&data=" + encryptedData));
	    return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
	}
}