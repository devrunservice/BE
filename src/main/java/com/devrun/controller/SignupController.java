package com.devrun.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.devrun.entity.MemberEntity;
import com.devrun.service.SignupService;

@Controller
public class SignupController {
	
	@Autowired
	SignupService signupService;

	@GetMapping("/signup")
	public String signup(HttpServletResponse response) {
		
		//Cache-Control 헤더는 클라이언트에 캐싱 지시문을 지정하는 데 사용됩니다. 이 예에서 제공되는 지시문은 다음과 같습니다.
		//no-store: 응답의 어떤 부분도 캐시나 중간 캐시에 저장하지 않도록 클라이언트에 지시합니다.
		//no-cache: 서버에서 먼저 재확인하지 않고 응답의 캐시된 버전을 사용해서는 안 된다고 클라이언트에 지시합니다.
		//must-revalidate: 캐시된 응답이 오래되면 서버에서 재검증해야 함을 클라이언트에 지시합니다.
		//max-age=0: 응답이 신선한 것으로 간주되는 최대 시간(초)을 지정합니다. 0으로 설정하면 응답이 즉시 부실해집니다.
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		//Pragma 헤더는 캐싱 지시문을 지정하는 데 사용되는 이전 HTTP/1.0 헤더입니다. 이 경우 'no-cache' 지시문이 제공됩니다. 
		//즉, 클라이언트는 서버에서 먼저 유효성을 다시 검사하지 않고 응답의 캐시된 버전을 사용해서는 안 됩니다. 
		//이 헤더는 주로 Cache-Control 헤더를 이해하지 못할 수 있는 이전 클라이언트와의 역호환성을 위해 포함됩니다.
	    response.setHeader("Pragma", "no-cache");
	    //'Expires' 헤더는 응답이 오래된 것으로 간주되는 날짜와 시간을 지정하는 데 사용됩니다. 에 의해
	    response.setHeader("Expires", "0");
	    
		return "signup";
	}
	
	@PostMapping("/signup/okay")
	public String okay(@ModelAttribute MemberEntity memberEntity) {
		// @ModelAttribute 어노테이션이 붙은 매개변수를 HTTP 요청 파라미터와 자동으로 바인딩하도록 지원합니다.
		// 이 기능은 HTML form 태그의 input 필드와 Java 객체의 필드를 매핑하여 사용하게 해줍니다.
		System.out.println(memberEntity);
		System.out.println(memberEntity.getEmail());
		Date currentDate = new Date();
		memberEntity.setSignup(currentDate);
		memberEntity.setLastlogin(currentDate);
		signupService.insert(memberEntity);
		return "okay";
	}
}
