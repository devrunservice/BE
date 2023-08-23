package com.devrun.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devrun.dto.OAuthToken;
import com.devrun.dto.member.KakaoProfileDTO;
import com.devrun.dto.member.LoginDTO;
import com.devrun.dto.member.LoginDTO.LoginStatus;
import com.devrun.dto.member.LogoutResponse;
import com.devrun.dto.member.SignupDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.LoginRepository;
import com.devrun.service.EmailSenderService;
import com.devrun.service.KakaoLoginService;
import com.devrun.service.LoginService;
import com.devrun.service.MemberService;
import com.devrun.service.TokenBlacklistService;
import com.devrun.util.JWTUtil;

import reactor.core.publisher.Mono;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private MemberService memberService;
    
    @Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
    private KakaoLoginService kakaoLoginService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    
    @Autowired
    private LoginRepository loginRepository;

    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_url}")
    private String redirect_uri;
    
    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request
						            , HttpServletResponse response
						            , @RequestBody MemberEntity memberEntity) {
    	
        System.out.println("Request URL: " + request.getRequestURL().toString());
        System.out.println("Remote Address: " + request.getRemoteAddr());

        MemberEntity member = new MemberEntity();

        member.setId(memberEntity.getId());
        member.setPassword(memberEntity.getPassword());
        
        System.out.println("1단계");
        if (memberService.validateId(member.getId()) 
//        		&& memberService.validatePassword(member.getPassword())										// 비밀번호 암호화 이후 검증 불가
        				) {

            LoginStatus status = loginService.validate(member);
            System.out.println("2단계");
            System.out.println("status : " + status);

            switch (status) {

                //	    토큰을 헤더에 담아 전송하는 것과 바디에 담아 전송하는 것은 각각 다른 상황과 목적에 적합합니다.
                //
                //	    1. 헤더에 토큰을 담아 전송하는 이유:
                //
                //	    표준화: Authorization 헤더를 사용하는 것은 웹 표준에 따른 방법입니다. 이를 통해 서버는 클라이언트로부터 받은 요청이 인증되었는지 쉽게 확인할 수 있습니다.
                //	    데이터 분리: 요청 헤더에 인증 정보를 담음으로써, 실제 요청 본문(body)은 비즈니스 로직과 관련된 데이터에만 집중할 수 있습니다. 이로 인해 관리가 용이해집니다.
                //	    보안: 헤더에 토큰을 담아 전송하면, 서버가 요청의 콘텐츠를 해석하기 전에 인증을 먼저 검사할 수 있습니다. 이는 보안 상의 이점을 제공합니다.
                //
                //	    2. 바디에 토큰을 담아 전송하는 이유:
                //
                //	    로그인과 토큰 생성: 로그인 시나리오에서는 사용자의 자격 증명(예: 사용자 이름과 비밀번호)을 서버에 전송해야 합니다. 이 자격 증명은 요청 본문에 담겨 있으며, 서버는 이 정보를 기반으로 토큰을 생성합니다. 로그인과 같은 특정 시나리오에서는 요청 본문을 사용하는 것이 더 적절합니다.
                //	    복잡한 데이터 구조: 때로는 복잡한 데이터 구조를 전송해야 하는데, 이 경우 요청 본문이 더 유연한 구조를 제공합니다. 헤더는 일반적으로 간단한 키-값 쌍이며, 복잡한 데이터를 담기에는 적합하지 않습니다.
                //	    결국, 로그인과 같이 토큰을 생성하거나 교환하는 데 필요한 정보를 보낼 때는 바디를 사용하고, 일단 토큰을 받아 인증이 필요한 API 요청을 할 때는 헤더에 토큰을 담아 전송하는 것이 일반적인 패턴입니다.

                case SUCCESS:

                    // 임시 토큰이 헤더에 있는지 확인
                    String easyloginToken = request.getHeader("Easylogin_token");

                    System.out.println("이지로그인 토큰 : " + easyloginToken);
                    if (easyloginToken != null && !easyloginToken.isEmpty()
                            && JWTUtil.validateToken(easyloginToken)
                    ) {
                        // 임시 토큰을 사용해 사용자 식별하고 로그인 과정 진행
                        String kakaoId = JWTUtil.getUserIdFromToken(easyloginToken);
                        String kakaoEmail = JWTUtil.getEmailFromEasyloginToken(easyloginToken);
                        // 여러개의 아이디에 연동하는 것을 막기위해 한번 더 체크
                        memberEntity = loginRepository.findByKakaoEmailId(kakaoId + kakaoEmail);
                        
                        if (memberEntity == null) {
                        	
                            // 로그인 성공 처리
                            memberEntity = loginRepository.findById(member.getId());
                            System.out.println("3단계" + memberEntity);

                            memberEntity.setKakaoEmailId(kakaoId + kakaoEmail);
                            System.out.println("간편로그인 아이디 설정 : " + memberEntity);

                            loginService.saveKakaoId(memberEntity);

                        } else {

                            // 이전에 생성된 로그아웃에 필요한 쿠키 삭제
                            loginService.deleteEasycookie(response);

                            // 400 연동 실패 : 이미 연동된 계정이 있음
                            return new ResponseEntity<>("Already linked to another user", HttpStatus.BAD_REQUEST);
                        }
                        
                    }

                    // 로그인 성공 처리
                    memberEntity = loginRepository.findById(member.getId());
                    System.out.println("3단계" + memberEntity);

                    // JWT토큰
                    String access_token = JWTUtil.generateAccessToken(memberEntity.getId(), memberEntity.getName());

                    // JWT refresh 토큰
                    String refresh_token = JWTUtil.generateRefreshToken(memberEntity.getId(), memberEntity.getName());


                    // 마지막 로그인 날짜 저장
                    loginService.setLastLogin(memberEntity);

                    // 로그인한 아이디의 이름 전달
                    LoginDTO loginDTO = new LoginDTO(status, "Login successful");

                    // 토큰을 응답 본문에 추가
                    loginDTO.setAccess_token("Bearer " + access_token);
//                    loginDTO.setRefresh_token("Bearer " + refresh_token);

                    // HTTPONLY 쿠키 생성
//                    loginService.setRefeshCookie(response, refresh_token);
                    ResponseCookie HTTP_refresh_token = loginService.setRefeshCookie(refresh_token);
                    
//                    String value = "Bearer " + refresh_token;
//            	    String encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
//                    ResponseCookie HTTP_refresh_token = ResponseCookie
//                    		.from("Refresh_token", encodedValue)
//                    		.path("/authz")
//                    		.sameSite("none")
//                    		.secure(true)
//                    		.httpOnly(true)
//                    		.build();
                    
                    // HttpHeaders 객체 생성 및 쿠키 설정
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Set-Cookie", HTTP_refresh_token.toString());

                    // 로그인 성공 200
                    return ResponseEntity.status(200).headers(headers).body(loginDTO);

                case USER_NOT_FOUND:
                case PASSWORD_MISMATCH:
                    // 401 로그인 실패 : 해당 유저가 존재하지 않음 or 비밀번호 불일치
                    return new ResponseEntity<>("User not found or Incorrect password", HttpStatus.UNAUTHORIZED);


                case ACCOUNT_INACTIVE:
                    //로그인 실패 401 : 회원 비활성화 상태
                    return new ResponseEntity<>(
                            //	            		new LoginDTO(status,
                            "Account is inactive"
                            //	            				)
                            , HttpStatus.UNAUTHORIZED);

                case ACCOUNT_WITHDRAWN:
                    //로그인 실패 403 : 탈퇴한 회원
                    return new ResponseEntity<>(
                            //	            		new LoginDTO(status,
                            "Account has been withdrawn"
                            //	            				)
                            , HttpStatus.FORBIDDEN);

                case LOGIN_TRIES_EXCEEDED:
                    //로그인 실패 403 : 로그인 5회이상 시도
                    return new ResponseEntity<>(
                            //	            		new LoginDTO(status,
                            "Login attempts exceeded"
                            //	            				)
                            , HttpStatus.FORBIDDEN);

                default:
                    // 로그인 실패 500 : 기타 실패 사례
                    return new ResponseEntity<>(
                            //	            		new LoginDTO(LoginStatus.USER_NOT_FOUND,
                            "Unknown error"
                            //	            				)
                            , HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            // 로그인 실패 401 : 아이디 또는 비밀번호 유효성 검사 실패
            return new ResponseEntity<>("Invalid userId or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @ResponseBody
    @PostMapping("/authz/token/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        // refreshToken이 헤더에 있는지 확인
//        String refreshToken = request.getHeader("Refresh_token");
    	Cookie[] cookies = request.getCookies();
	    System.out.println("리프레시 쿠키 : " + cookies);
	    String refreshToken = null;
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("Refresh_token".equals(cookie.getName())) {
	                String encodedRefreshToken = cookie.getValue();
	                refreshToken = new String(Base64.getDecoder().decode(encodedRefreshToken));
	            }
	        }
	    }
	    
//	    // Refresh Token 존재 여부 확인 (null 혹은 빈문자열 인지 확인)
        if (refreshToken == null || refreshToken.isEmpty()) {
            // 400 : Refresh token 없음
            return new ResponseEntity<>("Refresh token is required", HttpStatus.BAD_REQUEST);
        }
//	    String id = memberService.getIdFromToken(request);
        // 사용자 식별
        String userId = JWTUtil.getUserIdFromToken(refreshToken);
//        if (memberService.isUserIdEquals(userId)) {

            // Refresh Token 검증
//	    if (!JWTUtil.validateToken(refreshToken)) {
//	    	// 401 : 유효하지 않은 Refresh token
//	        return new ResponseEntity<>("Invalid refresh token", HttpStatus.UNAUTHORIZED);
//	    }

            // 사용자 존재 여부 확인
            MemberEntity memberEntity = loginRepository.findById(userId);
            if (memberEntity == null) {
                // 401 : 해당 유저가 존재하지 않음
                return new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED);
            }

            // 새로운 Access Token 생성
            String newAccessToken = JWTUtil.generateAccessToken(memberEntity.getId(), memberEntity.getName());
            String newRefreshToken = JWTUtil.generateRefreshToken(memberEntity.getId(), memberEntity.getName());

            // HttpOnly 쿠키 생성
//            loginService.setRefeshCookie(response, newRefreshToken);
            ResponseCookie HTTP_refresh_token = loginService.setRefeshCookie(newRefreshToken);
            
//            String value = "Bearer " + newRefreshToken;
//    	    String encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
//            ResponseCookie HTTP_refresh_token = ResponseCookie
//            		.from("Refresh_token", encodedValue)
//            		.path("/authz")
//            		.sameSite("none")
//            		.secure(true)
//            		.httpOnly(true)
//            		.build();
            
            // HttpHeaders 객체 생성 및 쿠키 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", HTTP_refresh_token.toString());
            
            // 새로운 Access Token 응답으로 전송
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("Access_token", "Bearer " + newAccessToken);
//            responseBody.put("Refresh_token", "Bearer " + newRefreshToken);

            // 200 : Access_token 발급
            return ResponseEntity.status(200).headers(headers).body(responseBody);
//        } else {
        	
            // 401 토큰의 사용자와 요청한 사용자 불일치
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request");
//        }
    }

	@ResponseBody
	@PostMapping("/authz/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
		
		// refreshToken이 헤더에 있는지 확인
//        String refreshToken = request.getHeader("Refresh_token");
		Cookie[] cookies = request.getCookies();
	    System.out.println("리프레시 쿠키 : " + cookies);
	    String refreshToken = null;
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("Refresh_token".equals(cookie.getName())) {
	                String encodedRefreshToken = cookie.getValue();
	                refreshToken = new String(Base64.getDecoder().decode(encodedRefreshToken));
	            }
	        }
	    }
        
//	    // Refresh Token 존재 여부 확인 (null 혹은 빈문자열 인지 확인)
        if (refreshToken == null || refreshToken.isEmpty()) {
            // 400 : Refresh token 없음
            return new ResponseEntity<>("Refresh token is required", HttpStatus.BAD_REQUEST);
        }
        
        // 사용자 식별
//        String userId = JWTUtil.getUserIdFromToken(refreshToken);
        
//        if (memberService.isUserIdEquals(userId)) {
	    
//		    // Refresh Token 검증
//		    if (!JWTUtil.validateToken(refreshToken)) {
//		        // 401 : 유효하지 않은 Refresh token
//		        return new ResponseEntity<>("Invalid refresh token", HttpStatus.UNAUTHORIZED);
//		    }

	        // 토큰을 블랙리스트에 추가합니다
//	        TokenBlacklist.blacklistToken(refreshToken);
        	tokenBlacklistService.blacklistToken(refreshToken);										// 테스트 끝나면 TokenBlacklist 삭제
	        
	        // 토큰이 블랙리스트에 올바르게 추가 됐는지 확인
//	        boolean isTokenBlacklisted = TokenBlacklist.isTokenBlacklisted(refreshToken);
//	        System.out.println("블랙리스트 등록 확인 : " + isTokenBlacklisted);
        	boolean isTokenBlacklisted = tokenBlacklistService.isTokenBlacklisted(refreshToken);
            System.out.println("블랙리스트 등록 확인 : " + isTokenBlacklisted);
	
	        // 200 : 로그아웃 성공
	        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
        
//        } else {
            // 401 토큰의 사용자와 요청한 사용자 불일치
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request");
//        }
        
    }
	
	@ResponseBody
	@GetMapping("/auth/kakao/callback")
	public ResponseEntity<?> kakaoCallback(@RequestParam(required = false) String code
									        , @RequestParam(required = false) String error
									        , HttpServletResponse response){
		
		System.out.println("코드" + code);
		
		// 성공했을 경우 code라는 파라미터값이 생성되고 실패했을 경우 error라는 파라미터값이 생성된다
		if ( code != null ) {
			
			// oauthToken 가져오기
			OAuthToken oauthToken = kakaoLoginService.getOauthToken(code);
			System.out.println("여기");
			
			if( oauthToken == null || oauthToken.getAccess_token() == null ) {
				
			// 500 : 엑세스 토큰을 가져오지 못 함
			return new ResponseEntity<>("Failed to retrieve access token", HttpStatus.INTERNAL_SERVER_ERROR);
			
			}
			
			System.out.println("카카오 엑세스 토큰 : " + oauthToken.getAccess_token());
			
			// kakaoProfile 가져오기
			KakaoProfileDTO kakaoProfile = kakaoLoginService.getKakaoProfile(oauthToken);
			System.out.println(kakaoProfile);
			
			if( kakaoProfile == null || kakaoProfile.getId() == null ) {
				
			// 500 : 카카오 프로필을 가져오지 못 함
			return new ResponseEntity<>("Failed to retrieve profile information", HttpStatus.INTERNAL_SERVER_ERROR);
			
			}
			
			System.out.println("카카오 ID : " + kakaoProfile.getId());
			System.out.println("카카오 Email : " + kakaoProfile.getKakao_account().getEmail());
			
			String kakaoId = kakaoProfile.getId().toString();
			String kakaoEmail = kakaoProfile.getKakao_account().getEmail();
			String KakaoEmailId = kakaoId + kakaoEmail;
			
			// SNS Access_token 생성
			loginService.setEasycookie(response, oauthToken.getAccess_token(), kakaoProfile.getId());
			
			MemberEntity memberEntity = new MemberEntity();
			
			memberEntity = loginRepository.findByKakaoEmailId(KakaoEmailId);
			System.out.println("3단계" + memberEntity);
			
			if ( memberEntity == null ) {
			
			// 카카오 계정에 연동된 아이디가 없는 경우
			// 카카오 아이디와 이메일을 포함한 토큰 생성
			String easylogin_token = JWTUtil.generateEasyloginToken(kakaoId, kakaoEmail);
			System.out.println("이지로그인 토큰 : " + easylogin_token);
			
			Map<String, String> response2 = new HashMap<>();
			response2.put("Easylogin_token", "Bearer " + easylogin_token);
			response2.put("message", "No linked account found. Please link your account.");
			System.out.println("간편로그인 리스폰스 : " + response2);
			
			// 303 : 연동된 계정이 존재하지 않음
			return new ResponseEntity<>(response2, HttpStatus.SEE_OTHER);
			
			} else {
			
			
			// JWT토큰
			String access_token = JWTUtil.generateAccessToken(memberEntity.getId(), memberEntity.getName());
			
			// JWT refresh 토큰
			String refresh_token = JWTUtil.generateRefreshToken(memberEntity.getId(), memberEntity.getName());
			
			// 마지막 로그인 날짜 저장
			loginService.setLastLogin(memberEntity);
			
			LoginStatus status = loginService.validate(memberEntity);
			
			// 로그인 정보 전달 객체 생성
			LoginDTO loginDTO = new LoginDTO(status, "KakaoLogin successful");
			
			// 토큰을 응답 본문에 추가
			loginDTO.setAccess_token("Bearer " + access_token);
//			loginDTO.setRefresh_token("Bearer " + refresh_token);
			
			// HttpOnly 쿠키 생성
//          loginService.setRefeshCookie(response, refresh_token);
			ResponseCookie HTTP_refresh_token = loginService.setRefeshCookie(refresh_token);
			
//			String value = "Bearer " + refresh_token;
//			String encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
//			ResponseCookie HTTP_refresh_token = ResponseCookie
//				.from("Refresh_token", encodedValue)
//				.path("/authz")
//				.sameSite("none")
//				.secure(true)
//				.httpOnly(true)
//				.build();
			  
			// HttpHeaders 객체 생성 및 쿠키 설정
			HttpHeaders headers = new HttpHeaders();
			headers.add("Set-Cookie", HTTP_refresh_token.toString());
			
			// 로그인 성공 200
			return ResponseEntity.status(200).headers(headers).body(loginDTO);
			
			}
		
		} else if ( error != null ) {
		
		// 400 로그인 에러
		return new ResponseEntity<>("KakaoLogin failed", HttpStatus.BAD_REQUEST);
		
		} else {
			
		// 400 code, error 둘다 null 값인 경우
		return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
		
		}

}
	
	@ResponseBody
	@PostMapping("/sns/logout/kakao")
	public ResponseEntity<?> kakaoLogout(HttpServletRequest request){
	
		String token = request.getHeader("Access_token_easy");
		String userIdString = request.getHeader("User_Id");
		
		if ( token == null ) {
			
		// 400 토큰 null
		return new ResponseEntity<>("Access_token_easy is null", HttpStatus.BAD_REQUEST);
		
		}
		
		if ( userIdString == null ) {
			
		// 400 id null
		return new ResponseEntity<>("ID is null", HttpStatus.BAD_REQUEST);
		
		}
		
		Long id = Long.parseLong(userIdString);
		LogoutResponse kakaoLogout = kakaoLoginService.kakaoLogout(token,id);
		
		System.out.println("로그아웃 성공 ID : " + kakaoLogout.getId());
		
		return new ResponseEntity<>("KakaoLogout successful", HttpStatus.OK);
	}
	
	//	@ResponseBody
	//	@GetMapping("/kakao/login")
	//	public RedirectView testLogin() {
	//		String test = "https://kauth.kakao.com/oauth/authorize?client_id=" + client_id + "&redirect_uri=" + redirect_uri + "&response_type=code";
	//		return new RedirectView(test);
	//	}
	
	@ResponseBody
	@PostMapping("/find/id")
	public Mono<ResponseEntity<?>>findId(@RequestBody SignupDTO signupDTO){
		
        String id = loginRepository.findByPhonenumber(signupDTO.getPhonenumber());
        System.out.println("찾은 아이디 : " + id + signupDTO.getCode());
        
        if(memberService.verifySmsCode(signupDTO.getPhonenumber(), signupDTO.getCode())){
        	
	        // id를 핸드폰번호로 발송
	        Mono<String> sendSmsResult = memberService.sendSmsFindid(signupDTO.getPhonenumber(), id);
	
	        // 메모리에 저장된 전화번호와 인증코드 제거
	        memberService.removeSmsCode(signupDTO.getPhonenumber());
        
        // .then은 실행되지면 return에는 무시되고 .just만 return에 포함된다 실행여부와 상관없이 (단지)just만 return 된다는 뜻이다
        return sendSmsResult.then(Mono.just(new ResponseEntity<>("Find ID successful", HttpStatus.OK)));

        } else {
        	
        // 403 인증되지 않은 전화번호
        return Mono.just(new ResponseEntity<>("Verification failed Phonenumber", HttpStatus.FORBIDDEN));
        
        }
    }
	
	@ResponseBody
	@PostMapping("/find/password")
	public ResponseEntity<?> findpw(@RequestBody SignupDTO signupDTO) {
		
        MemberEntity memberEntity = loginRepository.findById(signupDTO.getId());
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupDTO.getPassword());
        
        if (memberService.verifySmsCode(signupDTO.getPhonenumber(),signupDTO.getCode())
			&& memberService.validatePassword(signupDTO.getPassword())
			&& !memberEntity.getPassword().equals(encodedPassword)
			) {
        	
			memberEntity.setPassword(encodedPassword);
			
			loginRepository.save(memberEntity);
			
			memberService.removeSmsCode(signupDTO.getPhonenumber());
			
			return new ResponseEntity<>("Password change successful", HttpStatus.OK);
			
        } else if ( !memberService.verifySmsCode(signupDTO.getPhonenumber(), signupDTO.getCode()) ) {
        
        // 403 인증되지 않은 전화번호
        return new ResponseEntity<>("Verification failed Phonenumber", HttpStatus.FORBIDDEN);
        
        } else if ( memberEntity.getPassword().equals(encodedPassword) ) {
        
        // 400 현재 비밀번호와 같음
        return new ResponseEntity<>("Same as current password", HttpStatus.BAD_REQUEST);
        
        } else {
        
        // 400 비밀번호 유효성검사 실패
        return new ResponseEntity<>("Invalid input data", HttpStatus.BAD_REQUEST);
        
        }
    }
	
	@ResponseBody
	@PostMapping("/verifyPhone")
	public boolean verifyPhone(@RequestBody LoginDTO loginDTO){
		
		String id = loginDTO.getId();
		String phone = loginDTO.getPhonenumber();
		
		boolean isPhoneVerified = loginService.verifyPhone(id, phone);
		
		return isPhoneVerified;
	}
	
	@Autowired
	private EmailSenderService emailSenderService;
	
	@ResponseBody
	@PostMapping("/email")
	public ResponseEntity<?> email(@RequestParam("email") String toEmail
			, @RequestParam("id") String id
			, @RequestParam("nickname") String nickname) {
		
		String subject = "회원가입 이메일 인증 안내";
        String body = "Hi";
        try {
			
        emailSenderService.sendEmail(toEmail, subject, body);
		
		return ResponseEntity.status(200).body("테스트 성공");
        } catch (Exception e) {
        	System.out.println("이메일 에러 : " + e);
        	return ResponseEntity.status(403).body("테스트 실패");
        }
	}
	
}
