package com.devrun.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devrun.dto.OAuthToken;
import com.devrun.dto.member.KakaoProfileDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.service.KakaoLoginService;
import com.devrun.service.LoginService;

@Controller
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private KakaoLoginService kakaoLoginService;

	@GetMapping("/index")
	public String index() {
		
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@PostMapping("/login")
	public String login(HttpServletRequest request
						, @RequestParam("id") String id
						, @RequestParam("password") String password) {
		MemberEntity member = new MemberEntity();
		
		member.setId(id);
		member.setPassword(password);
		
		boolean success = loginService.validate(member);
		
		if (success) {
			HttpSession session = request.getSession();
			session.setAttribute("id", id);
			session.setAttribute("password", password);
			return "index";
		}else {
			//로그인 실패
			return "login";
		}
		
	}
	
	@ResponseBody
	@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(@RequestParam("code") String code) {
		
		// oauthToken 가져오기
		OAuthToken oauthToken = kakaoLoginService.getOauthToken(code);
        System.out.println("카카오 엑세스 토큰 : " + oauthToken.getAccess_token());
        
        // kakaoProfile 가져오기
		KakaoProfileDTO kakaoProfile = kakaoLoginService.getKakaoProfile(oauthToken);
		System.out.println("카카오 ID : " + kakaoProfile.getId());
		System.out.println("카카오 Email : " + kakaoProfile.getKakao_account().getEmail());
		
		return "카카오 프로필 : " + kakaoProfile;
	}
	
}