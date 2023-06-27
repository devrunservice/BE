package com.devrun.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.devrun.service.KakaoLoginService;
import com.devrun.service.LoginService;

@Controller
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private KakaoLoginService kakaoLoginService;
	
	@ResponseBody
	@GetMapping("/index")
	public String index() {
		return "index";
	}
	
	@ResponseBody
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@ResponseBody
	@PostMapping("/login")
	public ResponseEntity<LoginDTO> login(HttpServletRequest request
						, @RequestBody MemberEntity memberEntity) {
		
		MemberEntity member = new MemberEntity();
		
		member.setId(memberEntity.getId());
		member.setPassword(memberEntity.getPassword());
		System.out.println("1단계");
		LoginStatus status = loginService.validate(member);
		System.out.println("2단계");

	    switch (status) {
	    
	        case SUCCESS:
	        	System.out.println("3단계");
	            // 로그인 성공 처리
	        	Date currentDate = new Date();
	        	System.out.println("현재시간 : " + currentDate);
				memberEntity.setLastlogin(currentDate);
				loginService.save(memberEntity);
				
				HttpSession session = request.getSession();
				session.setAttribute("id", memberEntity.getId());
				
				// 로그인 성공 200
				return new ResponseEntity<>(new LoginDTO(status, "Login successful"), HttpStatus.OK);
				
	        case USER_NOT_FOUND:
	        	//로그인 실패 401 : 해당 유저가 존재하지 않음
	            return new ResponseEntity<>(new LoginDTO(status, "User not found"), HttpStatus.UNAUTHORIZED);
	            
	        case PASSWORD_MISMATCH:
	        	//로그인 실패 401 : 비밀번호가 일치하지 않음
	            return new ResponseEntity<>(new LoginDTO(status, "Incorrect password"), HttpStatus.UNAUTHORIZED);
	            
	        case ACCOUNT_INACTIVE:
	        	//로그인 실패 401 : 회원 비활성화 상태
	            return new ResponseEntity<>(new LoginDTO(status, "Account is inactive"), HttpStatus.UNAUTHORIZED);
	            
	        case ACCOUNT_WITHDRAWN:
	        	//로그인 실패 401 : 탈퇴한 회원
	            return new ResponseEntity<>(new LoginDTO(status, "Account has been withdrawn"), HttpStatus.UNAUTHORIZED);
	            
	        case LOGIN_TRIES_EXCEEDED:
	        	//로그인 실패 401 : 로그인 5회이상 시도
	            return new ResponseEntity<>(new LoginDTO(status, "Login attempts exceeded"), HttpStatus.UNAUTHORIZED);
	            
	        default:
	        	// 로그인 실패 401: 기타 실패 사례
	            return new ResponseEntity<>(new LoginDTO(LoginStatus.USER_NOT_FOUND, "Unknown error"), HttpStatus.UNAUTHORIZED);
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