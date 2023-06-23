package com.devrun.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.devrun.dto.OAuthToken;
import com.devrun.dto.member.KakaoProfileDTO;
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
	public String login(HttpServletRequest request
						, @RequestBody MemberEntity memberEntity) {
		
		MemberEntity member = new MemberEntity();
		
		member.setId(memberEntity.getId());
		member.setPassword(memberEntity.getPassword());
		
		boolean success = loginService.validate(member);
		
		if (success) {
			HttpSession session = request.getSession();
			session.setAttribute("id", memberEntity.getId());
			session.setAttribute("password", memberEntity.getPassword());
			return "index";
		}else {
			//로그인 실패
			return "login";
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