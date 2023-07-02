package com.devrun.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.devrun.entity.MemberEntity;
import com.devrun.repository.LoginRepository;
import com.devrun.service.KakaoLoginService;
import com.devrun.service.LoginService;
import com.devrun.util.JWTUtil;

@Controller
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private KakaoLoginService kakaoLoginService;
	
	@Autowired
	private LoginRepository loginRepository;
	
	@ResponseBody
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@ResponseBody
	@PostMapping("/login")
	public ResponseEntity<?> login(HttpServletRequest request
						, @RequestBody MemberEntity memberEntity) {
		
		MemberEntity member = new MemberEntity();
		
		member.setId(memberEntity.getId());
		member.setPassword(memberEntity.getPassword());
		System.out.println("1단계");
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
	        	// 로그인 성공 처리
	        	memberEntity = loginRepository.findById(member.getId());
	        	System.out.println("3단계" + memberEntity);

	        	// JWT토큰
	        	String access_token = JWTUtil.generateToken(memberEntity.getId());
	            
	            // 마지막 로그인 날짜 저장
	        	loginService.setLastLogin(memberEntity);
				
	        	// 로그인한 아이디의 이름 전달
	        	LoginDTO loginDTO = new LoginDTO(status, "Login successful", memberEntity.getName());
				
	        	// 토큰을 응답 본문에 추가
	            loginDTO.setAuthorization("Bearer " + access_token);
	            
	        	// 로그인 성공 200
				return new ResponseEntity<>(loginDTO, HttpStatus.OK);
				
	        case USER_NOT_FOUND:
	        	//로그인 실패 401 : 해당 유저가 존재하지 않음
	            return new ResponseEntity<>(
//	            		new LoginDTO(status, 
	            				"User not found"
//	            				)
	            		, HttpStatus.UNAUTHORIZED);
	            
	        case PASSWORD_MISMATCH:
	        	//로그인 실패 401 : 비밀번호가 일치하지 않음
	            return new ResponseEntity<>(
//	            		new LoginDTO(status,
	            				"Incorrect password"
//	            				)
	            		, HttpStatus.UNAUTHORIZED);
	            
	        case ACCOUNT_INACTIVE:
	        	//로그인 실패 401 : 회원 비활성화 상태
	            return new ResponseEntity<>(
//	            		new LoginDTO(status,
	            				"Account is inactive"
//	            				)
	            		, HttpStatus.UNAUTHORIZED);
	            
	        case ACCOUNT_WITHDRAWN:
	        	//로그인 실패 401 : 탈퇴한 회원
	            return new ResponseEntity<>(
//	            		new LoginDTO(status,
	            				"Account has been withdrawn"
//	            				)
	            		, HttpStatus.UNAUTHORIZED);
	            
	        case LOGIN_TRIES_EXCEEDED:
	        	//로그인 실패 401 : 로그인 5회이상 시도
	            return new ResponseEntity<>(
//	            		new LoginDTO(status, 
	            				"Login attempts exceeded"
//	            				)
	            		, HttpStatus.UNAUTHORIZED);
	            
	        default:
	        	// 로그인 실패 401: 기타 실패 사례
	            return new ResponseEntity<>(
//	            		new LoginDTO(LoginStatus.USER_NOT_FOUND,
	            				"Unknown error"
//	            				)
	            		, HttpStatus.UNAUTHORIZED);
	    }
	    
	}
	
	@ResponseBody
	@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(HttpServletRequest request
								, @RequestParam(required = false) String code
								, @RequestParam(required = false) String error) {
		
		// 성공했을 경우 code라는 파라미터값이 생성되고 실패했을 경우 error라는 파라미터값이 생성된다
		if (code != null) {
			
			// oauthToken 가져오기
			OAuthToken oauthToken = kakaoLoginService.getOauthToken(code);
	        System.out.println("카카오 엑세스 토큰 : " + oauthToken.getAccess_token());
	        HttpSession session = request.getSession();
	        // kakaoProfile 가져오기
			KakaoProfileDTO kakaoProfile = kakaoLoginService.getKakaoProfile(oauthToken);
			System.out.println("카카오 ID : " + kakaoProfile.getId());
			System.out.println("카카오 Email : " + kakaoProfile.getKakao_account().getEmail());
			
			session.setAttribute("Access_token", oauthToken.getAccess_token());
			session.setAttribute("id", kakaoProfile.getId());
			session.setAttribute("nickname", kakaoProfile.getProperties().getNickname());
			
			return "redirect:/index";
		} else if(error != null) {
			return "error";
		} else {
			// code, error 둘다 null 값인 경우 페이지
			return "error";
		}
		
	}
	
	@ResponseBody
	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		
		String token = (String) request.getSession().getAttribute("Access_token");
		Long id = (Long) request.getSession().getAttribute("id");
		LogoutResponse kakaoLogout = kakaoLoginService.kakaoLogout(token, id);
		
		// 세션 무효화
		HttpSession session = request.getSession();
	    if (session != null) {
	        session.invalidate();
	    }
		
		return "redirect:/login";
	}
}