package com.devrun.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devrun.dto.OAuthToken;
import com.devrun.dto.member.KakaoProfileDTO;
import com.devrun.dto.member.LoginDTO;
import com.devrun.dto.member.LoginDTO.LoginStatus;
import com.devrun.dto.member.LogoutResponse;
import com.devrun.dto.member.MemberDTO;
import com.devrun.dto.member.SignupDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.LoginRepository;
import com.devrun.service.EmailSenderService;
import com.devrun.service.KakaoLoginService;
import com.devrun.service.LoginService;
import com.devrun.service.MemberService;
import com.devrun.util.CaffeineCache;
import com.devrun.util.JWTUtil;
import com.devrun.util.RedisCache;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
	private EmailSenderService emailSenderService;
    
    @Autowired
//    private CaffeineCache redisCache;
    private RedisCache redisCache;
    
    @Autowired
    private LoginRepository loginRepository;
    
    @Autowired
	private EntityManager entityManager;
    
    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_url}")
    private String redirect_uri;
        
    @ResponseBody
    @PostMapping("/login")
    @ApiOperation(value = "사용자 로그인", notes = "사용자 ID와 비밀번호를 사용하여 로그인합니다.")
    @ApiImplicitParam(name = "MemberEntity", value = "id, password 전송", required = true, paramType = "body", dataTypeClass = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "로그인 성공"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 403, message = "접근 금지"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response, @RequestBody MemberEntity memberEntity) {
        logRequestInformation(request);

        MemberEntity member = createMemberEntityFromRequest(memberEntity);
        if (validateMember(member)) {
            return processLogin(member, request, response);
        } else {
            return invalidCredentialsResponse();
        }
    }

    private void logRequestInformation(HttpServletRequest request) {
        System.out.println("Request URL: " + request.getRequestURL().toString());
        System.out.println("Remote Address: " + request.getRemoteAddr());
    }

    private MemberEntity createMemberEntityFromRequest(MemberEntity memberEntity) {
        MemberEntity member = loginRepository.findById(memberEntity.getId());
        member.setId(memberEntity.getId());
        member.setPassword(memberEntity.getPassword());
        return member;
    }

    private boolean validateMember(MemberEntity member) {
        return memberService.validateId(member.getId()) && memberService.validatePassword(member.getPassword());
    }

    private ResponseEntity<?> processLogin(MemberEntity member, HttpServletRequest request, HttpServletResponse response) {
        LoginStatus status = loginService.validate(member);
        switch (status) {
            case SUCCESS:
                return handleSuccessLogin(member, request, response);
            case USER_NOT_FOUND:
            case PASSWORD_MISMATCH:
                return unauthorizedResponse("User not found or Incorrect password");
            case ACCOUNT_INACTIVE:
                return unauthorizedResponse("Account is inactive");
            case ACCOUNT_WITHDRAWN:
                return forbiddenResponse("Account has been withdrawn");
            case LOGIN_TRIES_EXCEEDED:
                return forbiddenResponse("Login attempts exceeded");
            default:
                return internalServerErrorResponse("Unknown error");
        }
    }

    private ResponseEntity<?> handleSuccessLogin(MemberEntity member, HttpServletRequest request, HttpServletResponse response) {
        // 이지로그인 토큰 유효성 검사
        if (isValidEasyLoginToken(request)) {
            // 이지로그인 처리
            if (!processEasyLogin(member, request, response)) {
                return new ResponseEntity<>("Already linked to another user", HttpStatus.BAD_REQUEST);
            }
        }

        // 로그인 마무리 및 토큰 생성
        member = finalizeLoginProcess(member);
        // LoginDTO에 SUCCESS 상태 설정
        LoginDTO loginDTO = new LoginDTO(LoginStatus.SUCCESS, "Login successful");

        // 토큰 생성 및 설정
        Map<String, String> tokens = generateTokens(member);
        loginDTO.setAccess_token("Bearer " + tokens.get("access_token"));

        // HTTPONLY 쿠키 설정
        HttpHeaders headers = setRefreshCookie(tokens.get("refresh_token"));

        // 성공적인 로그인 응답 반환
        return ResponseEntity.status(200).headers(headers).body(loginDTO);
    }
    
    private boolean processEasyLogin(MemberEntity member, HttpServletRequest request, HttpServletResponse response) {
    	String encodedPassword = passwordEncoder.encode(member.getPassword());
    	member.setPassword(encodedPassword);
        String easyloginToken = request.getHeader("Easylogin_token");
        String kakaoId = JWTUtil.getUserIdFromToken(easyloginToken);
        String kakaoEmail = JWTUtil.getEmailFromEasyloginToken(easyloginToken);
        // 여러개의 아이디에 연동하는 것을 막기위해 한번 더 체크
        MemberEntity existingMember = loginRepository.findByKakaoEmailId(kakaoId + kakaoEmail);
        System.out.println("저장전 : " + member);
        if (existingMember == null) {
            member.setKakaoEmailId(kakaoId + kakaoEmail);
            loginService.saveKakaoId(member);
            return true;
        } else {
            loginService.deleteEasycookie(response);
            return false;
        }
    }

    private MemberEntity finalizeLoginProcess(MemberEntity member) {
        // 마지막 로그인 날짜 저장
        loginService.setLastLogin(member);
        return loginRepository.findById(member.getId());
    }
    
    private Map<String, String> generateTokens(MemberEntity member) {
        // jti 생성
        String jti = JWTUtil.generateJti();
        // 액세스 토큰 생성
        String access_token = JWTUtil.generateAccessToken(member.getId(), member.getName(), jti);
        // 리프레시 토큰 생성
        String refresh_token = JWTUtil.generateRefreshToken(member.getId(), member.getName(), jti);

        // 토큰들을 맵에 저장
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);

        return tokens;
    }
    
    private HttpHeaders setRefreshCookie(String refreshToken) {
        // HTTPONLY 쿠키 생성
        ResponseCookie HTTP_refresh_token = loginService.setRefeshCookie(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        // 쿠키를 헤더에 추가
        headers.add("Set-Cookie", HTTP_refresh_token.toString());
        return headers;
    }
    
    private boolean isValidEasyLoginToken(HttpServletRequest request) {
        // 이지로그인 토큰 가져오기
        String easyloginToken = request.getHeader("Easylogin_token");
        // 이지로그인 토큰 유효성 검사
        return easyloginToken != null && !easyloginToken.isEmpty() && JWTUtil.validateToken(easyloginToken);
    }

    private ResponseEntity<?> invalidCredentialsResponse() {
        return new ResponseEntity<>("Invalid userId or password", HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<?> unauthorizedResponse(String message) {
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<?> forbiddenResponse(String message) {
        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<?> internalServerErrorResponse(String message) {
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 엑세스토큰 재발급
    @ResponseBody
    @PostMapping("/authz/token/refresh")
    @ApiOperation(value = "토큰 리프레시", notes = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 생성합니다. 리프레시 토큰은 HttpOnly 쿠키에 저장되어 있습니다.")
    @ApiImplicitParam(name = "Refresh_token", value = "리프레시 토큰", required = true, paramType = "header", dataTypeClass = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "토큰 리프레시 성공"),
        @ApiResponse(code = 400, message = "잘못된 요청"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 500, message = "서버 오류")})
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // 요청에서 리프레시 토큰 가져오기
        String refreshToken = getRefreshTokenFromRequest(request);

        // 리프레시 토큰 유효성 검사
        if (refreshToken == null) {
            return new ResponseEntity<>("Refresh token is required", HttpStatus.BAD_REQUEST);
        }

        // 토큰에서 사용자 ID 추출
        String userId = JWTUtil.getUserIdFromToken(refreshToken);
        // DB에서 사용자 정보 조회
        MemberEntity memberEntity = loginRepository.findById(userId);

        // 사용자 유효성 검사
        if (memberEntity == null) {
            return new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED);
        }

        // Redis에서 jti 업데이트
        String jti = updateJtiInRedis(userId);

        // 새 토큰 생성
        Map<String, String> newTokens = generateNewTokens(memberEntity, jti);
        // 새 리프레시 토큰으로 쿠키 설정
        HttpHeaders headers = setNewRefreshCookie(newTokens.get("refresh_token"));

        // 응답 바디 설정
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("Access_token", "Bearer " + newTokens.get("access_token"));

        return ResponseEntity.status(200).headers(headers).body(responseBody);
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        // 쿠키에서 리프레시 토큰 추출
        Cookie[] cookies = request.getCookies();
        return JWTUtil.getRefreshTokenFromCookies(cookies);
    }

    private String updateJtiInRedis(String userId) {
        // Redis에서 기존 jti 삭제
        redisCache.removeJti(userId);
        // 새 jti 생성
        String newJti = JWTUtil.generateJti();
        // Redis에 새 jti 저장
        redisCache.setJti(userId, newJti);
        return newJti;
    }

    private Map<String, String> generateNewTokens(MemberEntity memberEntity, String jti) {
        // 새 액세스 토큰과 리프레시 토큰 생성
        String newAccessToken = JWTUtil.generateAccessToken(memberEntity.getId(), memberEntity.getName(), jti);
        String newRefreshToken = JWTUtil.generateRefreshToken(memberEntity.getId(), memberEntity.getName(), jti);

        Map<String, String> newTokens = new HashMap<>();
        newTokens.put("access_token", newAccessToken);
        newTokens.put("refresh_token", newRefreshToken);
        return newTokens;
    }

    private HttpHeaders setNewRefreshCookie(String newRefreshToken) {
        // 새 리프레시 토큰으로 HTTPONLY 쿠키 생성
        ResponseCookie newCookie = loginService.setRefeshCookie(newRefreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", newCookie.toString());
        return headers;
    }
    
    // 블랙리스트에 리프래시 토큰을 등록해 엑세스 토큰을 재발급 받지 못하도록 합니다.
    @ResponseBody
    @PostMapping("/authz/logout")
    @ApiOperation(value = "로그아웃", notes = "현재 로그인된 사용자를 로그아웃합니다. 리프레시 토큰은 HttpOnly 쿠키에 저장되어 있습니다.")
    @ApiImplicitParam(name = "Refresh_token", value = "리프레시 토큰", required = true, paramType = "header", dataTypeClass = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "로그아웃 성공"),
        @ApiResponse(code = 400, message = "잘못된 요청"),
        @ApiResponse(code = 500, message = "서버 오류")})
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // 요청에서 리프레시 토큰 추출
        String refreshToken = getRefreshTokenFromCookies(request);

        // 리프레시 토큰 유효성 검사
        if (refreshToken == null) {
            return new ResponseEntity<>("Refresh token is required", HttpStatus.BAD_REQUEST);
        }

        // 토큰에서 사용자 ID 추출
        String userId = JWTUtil.getUserIdFromToken(refreshToken);

        // Redis에서 jti 제거
        removeJtiFromRedis(userId);

        // 토큰 블랙리스트 등록
        blacklistToken(refreshToken);

        // 로그아웃 성공 응답
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        // 쿠키에서 리프레시 토큰 추출
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Refresh_token".equals(cookie.getName())) {
                    refreshToken = new String(Base64.getDecoder().decode(cookie.getValue()));
                }
            }
        }
        return refreshToken;
    }

    private void removeJtiFromRedis(String userId) {
        // Redis에서 jti 정보 제거
        redisCache.removeJti(userId);
    }

    private void blacklistToken(String refreshToken) {
        // 토큰을 블랙리스트에 등록
        redisCache.blacklistToken(refreshToken);
    }
    
    @ResponseBody
    @GetMapping("/auth/kakao/callback")
    @ApiOperation(value = "카카오 로그인 콜백", notes = "카카오 로그인 후 콜백 URL입니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "code", value = "인증 코드", required = false, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "error", value = "에러 메시지", required = false, paramType = "query", dataTypeClass = String.class)})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "카카오 로그인 성공"),
        @ApiResponse(code = 400, message = "잘못된 요청"),
        @ApiResponse(code = 500, message = "서버 오류")})
    public ResponseEntity<?> kakaoCallback(@RequestParam(required = false) String code,
                                           @RequestParam(required = false) String error,
                                           HttpServletResponse response) {
        // 에러가 있는 경우
        if (error != null) {
            return new ResponseEntity<>("KakaoLogin failed", HttpStatus.BAD_REQUEST);
        }
        
        // code가 없는 경우
        if (code == null) {
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        }
        
        // OAuth 토큰을 가져옵니다.
        OAuthToken oauthToken = kakaoLoginService.getOauthToken(code);
        if (isOauthTokenInvalid(oauthToken)) {
            return new ResponseEntity<>("Failed to retrieve access token", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 카카오 프로필을 가져옵니다.
        KakaoProfileDTO kakaoProfile = kakaoLoginService.getKakaoProfile(oauthToken);
        if (isProfileInvalid(kakaoProfile)) {
            return new ResponseEntity<>("Failed to retrieve profile information", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return handleKakaoLogin(kakaoProfile, oauthToken, response);
    }

    private boolean isOauthTokenInvalid(OAuthToken oauthToken) {
        return oauthToken == null || oauthToken.getAccess_token() == null;
    }

    private boolean isProfileInvalid(KakaoProfileDTO kakaoProfile) {
        return kakaoProfile == null || kakaoProfile.getId() == null;
    }

    private ResponseEntity<?> handleKakaoLogin(KakaoProfileDTO kakaoProfile, OAuthToken oauthToken, HttpServletResponse response) {
        String kakaoId = kakaoProfile.getId().toString();
        String kakaoEmail = kakaoProfile.getKakao_account().getEmail();
        String KakaoEmailId = kakaoId + kakaoEmail;

        // SNS Access_token 생성
        loginService.setEasycookie(response, oauthToken.getAccess_token(), kakaoProfile.getId());

        MemberEntity memberEntity = loginRepository.findByKakaoEmailId(KakaoEmailId);

        if (memberEntity == null) {
            String easylogin_token = JWTUtil.generateEasyloginToken(kakaoId, kakaoEmail);
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("Easylogin_token", "Bearer " + easylogin_token);
            responseMap.put("message", "No linked account found. Please link your account.");
            return new ResponseEntity<>(responseMap, HttpStatus.SEE_OTHER);
        } else {
            // jti 생성
            String jti = JWTUtil.generateJti();
            
            // Access_token 생성
            String access_token = JWTUtil.generateAccessToken(memberEntity.getId(), memberEntity.getName(), jti);

            // Refresh_token 생성
            String refresh_token = JWTUtil.generateRefreshToken(memberEntity.getId(), memberEntity.getName(), jti);

            // 마지막 로그인 날짜 저장
            loginService.setLastLogin(memberEntity);

            // 로그인 상태 검증
            LoginStatus status = loginService.validate(memberEntity);
            
            // 로그인 정보 전달 객체 생성
            LoginDTO loginDTO = new LoginDTO(status, "KakaoLogin successful");

            // 토큰을 응답 본문에 추가
            loginDTO.setAccess_token("Bearer " + access_token);

            // HttpOnly 쿠키 생성
            ResponseCookie HTTP_refresh_token = loginService.setRefeshCookie(refresh_token);

            // HttpHeaders 객체 생성 및 쿠키 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", HTTP_refresh_token.toString());

            // 로그인 성공 200
            return ResponseEntity.status(200).headers(headers).body(loginDTO);
        }
    }

    @ResponseBody
    @PostMapping("/sns/logout/kakao")
    @ApiOperation(value = "카카오 로그아웃", notes = "카카오 로그아웃을 수행합니다.")
    @ApiImplicitParam(name = "Access_token_easy", value = "액세스 토큰", required = true, paramType = "header", dataTypeClass = String.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "카카오 로그아웃 성공"),
        @ApiResponse(code = 400, message = "잘못된 요청"),
        @ApiResponse(code = 403, message = "로그아웃 실패"),
        @ApiResponse(code = 500, message = "내부 서버 오류")})
    public ResponseEntity<?> kakaoLogout(HttpServletRequest request) {
        // Access_token_easy 헤더를 가져옵니다.
        String token = request.getHeader("Access_token_easy");
        if (token == null) {
            // 400 : Access_token_easy가 null인 경우
            return new ResponseEntity<>("Access_token_easy is null", HttpStatus.BAD_REQUEST);
        }

        // User_Id 헤더를 가져옵니다.
        String userIdString = request.getHeader("User_Id");
        if (userIdString == null) {
            // 400 : User_Id가 null인 경우
            return new ResponseEntity<>("ID is null", HttpStatus.BAD_REQUEST);
        }

        // String 형태의 userId를 Long 형태로 변환
        Long id = Long.parseLong(userIdString);

        // 카카오 로그아웃 처리
        LogoutResponse kakaoLogout = kakaoLoginService.kakaoLogout(token, id);

        // 로그아웃 성공시 로그 출력
        System.out.println("로그아웃 성공 ID : " + kakaoLogout.getId());

        // 200 : 카카오 로그아웃 성공
        return new ResponseEntity<>("KakaoLogout successful", HttpStatus.OK);
    }

	// 회원의 핸드폰 번호가 맞는지 확인
	@ResponseBody
	@PostMapping("/users/{userId}/verify/phone")
	@ApiOperation(value = "핸드폰 번호 검증", notes = "회원의 핸드폰 번호가 맞는지 검증합니다.")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "userId", value = "사용자 아이디", required = true, paramType = "path", dataTypeClass = String.class),
	    @ApiImplicitParam(name = "phonenumber", value = "핸드폰 번호", required = true, paramType = "body", dataTypeClass = String.class)})
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "핸드폰 번호 검증 성공"),
	    @ApiResponse(code = 400, message = "잘못된 요청"),
	    @ApiResponse(code = 403, message = "검증 실패"),
	    @ApiResponse(code = 500, message = "내부 서버 오류")})
	public boolean verifyPhone(@PathVariable String userId, @RequestBody LoginDTO loginDTO){
		
		boolean isPhoneVerified = loginService.verifyPhone(userId, loginDTO.getPhonenumber());
		
		return isPhoneVerified;
	}
	
	// 찾은 아이디를 핸드폰 번호로 전송
	@ResponseBody
	@PostMapping("/find-id/send-phone")
	@ApiOperation(value = "아이디 핸드폰 번호로 전송", notes = "찾은 아이디를 핸드폰 번호로 전송합니다.")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "phonenumber", value = "핸드폰 번호", required = true, paramType = "body", dataTypeClass = String.class),
	    @ApiImplicitParam(name = "code", value = "인증 코드", required = true, paramType = "body", dataTypeClass = String.class)})
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "아이디 전송 성공"),
	    @ApiResponse(code = 400, message = "잘못된 요청"),
	    @ApiResponse(code = 403, message = "전송 실패"),
	    @ApiResponse(code = 500, message = "내부 서버 오류")})
	public Mono<ResponseEntity<?>>sendIdBySms(@RequestBody SignupDTO signupDTO){
		
        String id = loginRepository.findByPhonenumber(signupDTO.getPhonenumber());
        System.out.println("찾은 아이디 : " + id + signupDTO.getCode());
        
        if(memberService.verifyCode(signupDTO.getPhonenumber(), signupDTO.getCode())){
        	
	        // id를 핸드폰번호로 발송
	        Mono<String> sendSmsResult = memberService.sendSmsFindId(signupDTO.getPhonenumber(), id);
	
	        // 메모리에 저장된 전화번호와 인증코드 제거
	        memberService.removeVerifyCode(signupDTO.getPhonenumber());
        
        // .then은 실행되지면 return에는 무시되고 .just만 return에 포함된다 실행여부와 상관없이 (단지)just만 return 된다는 뜻이다
        return sendSmsResult.then(Mono.just(new ResponseEntity<>("Find ID successful", HttpStatus.OK)));

        } else {
        	
        // 403 인증되지 않은 전화번호
        return Mono.just(new ResponseEntity<>("Verification failed Phonenumber", HttpStatus.FORBIDDEN));
        
        }
    }
	
	// 인증 완료 후 비밀번호 변경
	@ResponseBody
	@PostMapping("/users/{userId}/edit-password")
	@ApiOperation(value = "비밀번호 변경", notes = "인증 완료 후 비밀번호를 변경합니다.")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "userId", value = "사용자 아이디", required = true, paramType = "path", dataTypeClass = String.class),
	    @ApiImplicitParam(name = "password", value = "새 비밀번호", required = true, paramType = "body", dataTypeClass = String.class),
	    @ApiImplicitParam(name = "phonenumber", value = "핸드폰 번호", required = false, paramType = "body", dataTypeClass = String.class),
	    @ApiImplicitParam(name = "email", value = "이메일", required = false, paramType = "body", dataTypeClass = String.class),
	    @ApiImplicitParam(name = "code", value = "인증 코드", required = true, paramType = "body", dataTypeClass = String.class)})
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "비밀번호 변경 성공"),
	    @ApiResponse(code = 400, message = "잘못된 요청"),
	    @ApiResponse(code = 403, message = "변경 실패"),
	    @ApiResponse(code = 500, message = "내부 서버 오류")})
	public ResponseEntity<?> editPassword(@PathVariable String userId, @RequestBody SignupDTO signupDTO) {
		
        MemberEntity memberEntity = loginRepository.findById(userId);
        System.out.println("회원 : " + memberEntity);
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupDTO.getPassword());
        
        String key;
        boolean hasPhoneNumber = signupDTO.getPhonenumber() != null;
        boolean hasEmail = signupDTO.getEmail() != null;

        if (hasPhoneNumber ^ hasEmail) {  // XOR 연산
            key = hasPhoneNumber ? signupDTO.getPhonenumber() : signupDTO.getEmail();
            System.out.println("hasPhoneNumber : " + hasPhoneNumber);
            System.out.println("hasEmail : " + hasEmail);
            System.out.println("key : " + key);
        } else {
        	// 400 : 이메일과 전화번호 중 하나만 제공되어야 함
            return new ResponseEntity<>("Either Email or Phonenumber should be provided, not both or none.", HttpStatus.BAD_REQUEST);
        }
        System.out.println("1 : " + memberService.verifyCode(key, signupDTO.getCode()));
        System.out.println("2 : " + memberService.validatePassword(signupDTO.getPassword()));
        System.out.println("3 : " + memberEntity.getPassword());
        // 코드 검증, 비밀번호 유효성 검증, 현재 비밀번호와의 비교
        if (memberService.verifyCode(key, signupDTO.getCode())
        		&& memberService.validatePassword(signupDTO.getPassword())
        		&& !memberEntity.getPassword().equals(encodedPassword)
			) {
        	// 비밀번호 변경 및 저장
			memberEntity.setPassword(encodedPassword);
			loginRepository.save(memberEntity);
			// 인증 코드 제거
			memberService.removeVerifyCode(key);
			// 200 : 비밀번호 변경 성공
			return new ResponseEntity<>("Password change successful", HttpStatus.OK);
			
        } else if ( !memberService.verifyCode(signupDTO.getPhonenumber(), signupDTO.getCode()) ) {
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
	
	// 회원의 이메일이 맞는지 확인
	@ResponseBody
	@PostMapping("/users/{userId}/verify/email")
	@ApiOperation(value = "이메일 검증", notes = "회원의 이메일이 맞는지 검증합니다.")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "userId", value = "사용자 아이디", required = true, paramType = "path", dataTypeClass = String.class),
	    @ApiImplicitParam(name = "email", value = "이메일 주소", required = true, paramType = "body", dataTypeClass = String.class)})
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "이메일 검증 성공"),
	    @ApiResponse(code = 400, message = "잘못된 요청"),
	    @ApiResponse(code = 403, message = "검증 실패"),
	    @ApiResponse(code = 500, message = "내부 서버 오류")})
	public boolean verifyEmail(@PathVariable String userId, @RequestBody LoginDTO loginDTO){
		
		boolean isEmailVerified = loginService.verifyEmail(userId, loginDTO.getEmail());
		
		return isEmailVerified;
	}
	
	// 비밀번호 찾기 인증번호 이메일로 전송
	@ResponseBody
	@PostMapping("/auth/email")
	@ApiOperation(value = "인증번호 이메일로 전송", notes = "비밀번호 찾기를 위한 인증번호를 이메일로 전송합니다.")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "email", value = "이메일 주소", required = true, paramType = "body", dataTypeClass = String.class),
	    @ApiImplicitParam(name = "id", value = "사용자 아이디", required = true, paramType = "body", dataTypeClass = String.class)})
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "이메일 전송 성공"),
	    @ApiResponse(code = 400, message = "잘못된 요청"),
	    @ApiResponse(code = 403, message = "이메일 전송 실패"),
	    @ApiResponse(code = 500, message = "내부 서버 오류")})
	public ResponseEntity<?> signupEmail(@RequestBody MemberDTO memberDTO) {
        try {
        	emailSenderService.sendFindByEmail(memberDTO.getEmail(), memberDTO.getId());
        	// 200 성공
        	return ResponseEntity.status(200).body("Email sent successfully");
        } catch (Exception e) {
        	// 403 이메일 전송 실패
        	return ResponseEntity.status(403).body("Failed to send email");
        }
	}
	
	// 이메일로 전송된 인증코드 맞는지 검증
	@ResponseBody
	@PostMapping("/verify/email")
	@ApiOperation(value = "이메일 인증코드 검증", notes = "이메일로 전송된 인증코드가 맞는지 검증합니다.")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "email", value = "이메일 주소", required = true, paramType = "body", dataTypeClass = String.class),
	    @ApiImplicitParam(name = "code", value = "인증 코드", required = true, paramType = "body", dataTypeClass = String.class)})
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "인증 성공"),
	    @ApiResponse(code = 400, message = "잘못된 요청"),
	    @ApiResponse(code = 403, message = "인증 실패"),
	    @ApiResponse(code = 500, message = "내부 서버 오류")})
	public ResponseEntity<?> verify(@RequestBody SignupDTO signupDTO) {
        if (memberService.verifyCode(signupDTO.getEmail(), signupDTO.getCode())) {
        	// 200 인증성공
        	return new ResponseEntity<>("Verification successful", HttpStatus.OK);
        } else {
        	// 403 인증실패
        	return new ResponseEntity<>("Verification failed", HttpStatus.FORBIDDEN);
        }
    }
	
	// 이메일로 아이디 전송
	@ResponseBody
	@PostMapping("/find-id/send-email")
	@ApiOperation(value = "이메일로 아이디 전송", notes = "이메일로 사용자 아이디를 전송합니다.")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "email", value = "이메일 주소", required = true, paramType = "body", dataTypeClass = String.class),
	    @ApiImplicitParam(name = "code", value = "인증 코드", required = true, paramType = "body", dataTypeClass = String.class)})
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "아이디 찾기 성공"),
	    @ApiResponse(code = 400, message = "잘못된 요청"),
	    @ApiResponse(code = 403, message = "인증 실패"),
	    @ApiResponse(code = 500, message = "내부 서버 오류")})
	public ResponseEntity<?> findIdEmail(@RequestBody SignupDTO signupDTO){
		
        String id = loginRepository.findByEmail(signupDTO.getEmail());
        System.out.println("찾은 아이디 : " + id + signupDTO.getCode());
        
        if(memberService.verifyCode(signupDTO.getEmail(), signupDTO.getCode())){
	        // id를 이메일로 발송
	        emailSenderService.sendIdByEmail(signupDTO.getEmail(), id);
	        // 메모리에 저장된 이메일과 인증코드 제거
	        memberService.removeVerifyCode(signupDTO.getEmail());
	        // .then은 실행되지면 return에는 무시되고 .just만 return에 포함된다 실행여부와 상관없이 (단지)just만 return 된다는 뜻이다
	        return new ResponseEntity<>("Find ID successful", HttpStatus.OK);
        } else {
	        // 403 인증되지 않은 전화번호
	        return new ResponseEntity<>("Verification failed Email", HttpStatus.FORBIDDEN);
        }
    }
	
	// 사용자 로그인 정보 조회
	@ResponseBody
	@GetMapping("/users/login-info")
	@ApiOperation(value = "사용자 로그인 정보 조회", notes = "사용자의 로그인 정보를 조회하고 로그인 상태를 유지하기 위해 사용합니다.")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "Access_token", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class)})
	@ApiResponses(value = {
	    @ApiResponse(code = 200, message = "정보 조회 성공"),
	    @ApiResponse(code = 400, message = "잘못된 요청"),
	    @ApiResponse(code = 403, message = "인증 실패"),
	    @ApiResponse(code = 500, message = "내부 서버 오류")})
	public ResponseEntity<?> infoRole(HttpServletRequest request){
		// AccessToken이 헤더에 있는지 확인
		String accessToken = request.getHeader("Access_token");

	    // AccessToken 존재 여부 확인 (null 혹은 빈문자열 인지 확인)
		if (accessToken == null || accessToken.isEmpty()) {
			// 400 : Access token 없음
			return new ResponseEntity<>("Access token is required", HttpStatus.BAD_REQUEST);
		}
		
		String id = JWTUtil.getUserIdFromToken(accessToken);
		
		MemberEntity member = memberService.findById(id);
		
		MemberDTO memberDTO = new MemberDTO();
		
		if (member != null) {
			memberDTO.setUserNo(member.getUserNo());
			memberDTO.setId(member.getId());
			memberDTO.setRole(member.getRole());
		}
		
		return new ResponseEntity<>(memberDTO, HttpStatus.OK);
	}
	
	@Autowired
    private CaffeineCache caffeineCache;
	
	// 이거 왜 만들었지 뭔가 임시로 만든 거 같은데 뭐지
	// 이것저것 섞인 것 같은데 비밀번호 변경이겠지 하고 일단 수정하고 냅둠
	// 아닌데 뭐지 그냥 테스트 였나
	// 얘 진짜 뭐야 진짜 테스트인가
	@ResponseBody
	@PostMapping("/signup/ok")
	public ResponseEntity<?> signupOk(@RequestBody MemberDTO memberDTO
										, @RequestParam("key") String key){
		
		MemberEntity member = memberService.findById(memberDTO.getId());
		String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
		if (member != null) {
			
			String cachedKey = caffeineCache.getCaffeine(memberDTO.getId());
			if (cachedKey != null && cachedKey.equals(key)) {
				member.setPassword(encodedPassword);
				memberService.insert(member);
				caffeineCache.removeCaffeine(memberDTO.getId());
				
				return ResponseEntity.status(200).body("Edit password successful");
			} else {
				return ResponseEntity.status(400).body("Authentication key mismatch");
			}
				
		} else {
			// 회원을 찾을 수 없음
			return ResponseEntity.status(404).body("Member not found");
		}
	}

}