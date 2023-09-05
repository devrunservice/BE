package com.devrun.service;

import java.util.Base64;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devrun.dto.member.LoginDTO.LoginStatus;
import com.devrun.dto.member.MemberDTO.Status;
import com.devrun.entity.Contact;
import com.devrun.entity.LoginInfo;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.ContactRepository;
import com.devrun.repository.LoginInfoRepository;
import com.devrun.repository.LoginRepository;
import com.devrun.repository.MemberEntityRepository;

@Service
public class LoginService {
	
	@Autowired
	private LoginRepository loginRepository;
	
	@Autowired
	private MemberEntityRepository memberEntityRepository;
	
	@Autowired
	private LoginInfoRepository loginInfoRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private LoginStatus loginStatus;

	// 마지막 로그인 날짜 수정
	public void setLastLogin(MemberEntity memberEntity) {
		Date currentDate = new Date();
    	System.out.println("현재시간 : " + currentDate);
    	LoginInfo loginInfo = loginInfoRepository.findByMemberEntity(memberEntity);
        if (loginInfo == null) {
            loginInfo = new LoginInfo();
            loginInfo.setMemberEntity(memberEntity);
        }

        loginInfo.setLastlogin(currentDate);
        loginInfoRepository.save(loginInfo);
	}
	
	public LoginStatus validate(MemberEntity member) {
		
		MemberEntity existingMember = loginRepository.findById(member.getId());
		LoginInfo existingLoginInfo = loginInfoRepository.findByMemberEntity(existingMember);
        
		System.out.println("회원정보 : " + existingMember + "\n입력한 회원정보 : " + member);
		
		if (existingMember == null) {
		    return LoginStatus.USER_NOT_FOUND;
		} else if (existingLoginInfo.getLogintry() >= 5) {
		    return LoginStatus.LOGIN_TRIES_EXCEEDED;
		} else if (!passwordEncoder.matches(member.getPassword(), existingMember.getPassword())) {
			existingLoginInfo.setLogintry(existingLoginInfo.getLogintry() + 1);
		    memberEntityRepository.save(existingMember);
		    return LoginStatus.PASSWORD_MISMATCH;
		} else if (existingMember.getStatus() == Status.INACTIVE) {
		    return LoginStatus.ACCOUNT_INACTIVE;
		} else if (existingMember.getStatus() == Status.WITHDRAWN) {
		    return LoginStatus.ACCOUNT_WITHDRAWN;
		} else {
			existingLoginInfo.setLogintry(existingLoginInfo.getLogintry() * 0); // reset login tries on successful login
		    memberEntityRepository.save(existingMember);
		    return LoginStatus.SUCCESS;
		}
	}
	public void saveKakaoId(MemberEntity memberEntity) {
		memberEntityRepository.save(memberEntity);
	}
	
	public boolean verifyPhone(String id, String phonenumber) {
	    MemberEntity member = memberEntityRepository.findById(id);
	    if (member == null) {
	        return false;
	    }
	    Contact existingContact = contactRepository.findByMemberEntity(member);
	    if (existingContact == null) {
	        return false;
	    }
	    return existingContact.getPhonenumber().equals(phonenumber);
	}
	
	public boolean verifyEmail(String id, String email) {
		MemberEntity member = memberEntityRepository.findById(id);
	    if (member == null) {
	        return false;
	    }
	    Contact existingContact = contactRepository.findByMemberEntity(member);
	    if (existingContact == null) {
	        return false;
	    }
		return existingContact.getEmail().equals(email);
	}
	
	// Refresh_token HttpOnly 쿠키 생성
//	public void setRefeshcookie(HttpServletResponse response, String token) {
//		
//		String value = "Bearer " + token;
//	    String encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
//	    Cookie Refresh_token = new Cookie("Refresh_token", encodedValue);
//	    Refresh_token.setHttpOnly(true);
//	    Refresh_token.setMaxAge(24 * 60 * 60);
//	    Refresh_token.setPath("/");
//	    Refresh_token.setDomain("devrun.net");
//	    Refresh_token.setSecure(true);
//	    response.addCookie(Refresh_token);
//	    
//	}
	public ResponseCookie setRefeshCookie(String token) {
		
		String value = "Bearer " + token;
		String encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
		ResponseCookie refresh_token = ResponseCookie
			.from("Refresh_token", encodedValue)
//			.domain("devrun.site")
			.path("/authz")
			.sameSite("none")
//			.secure(true)
			.httpOnly(true)
			.build();
		
		System.out.println("리프레시 토큰 생성 : " + refresh_token);
		
		return refresh_token;
	}
	
	// 로그아웃에 필요한 SNS Access_token 생성
	public void setEasycookie(HttpServletResponse response, String token, Long id) {
		
		String value = "Bearer " + token;
	    String encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
		Cookie SNSaccessToken = new Cookie("Access_token_easy", encodedValue);
		SNSaccessToken.setHttpOnly(true);
		SNSaccessToken.setPath("/sns/logout");
	    response.addCookie(SNSaccessToken);
	    
	    Cookie userId = new Cookie("User_Id", id.toString());
	    userId.setHttpOnly(true);
	    userId.setPath("/sns/logout");
	    response.addCookie(userId);
	}
	
	// 로그아웃에 필요한 SNS Access_token 삭제
	public void deleteEasycookie(HttpServletResponse response) {
		
		Cookie SNSaccessToken = new Cookie("Access_token_easy", null); // Same name and path
		SNSaccessToken.setPath("/sns/logout");
		SNSaccessToken.setMaxAge(0); // Set Max-Age to 0
	    response.addCookie(SNSaccessToken);
	    
	    Cookie userId = new Cookie("User_Id", null);
	    userId.setPath("/sns/logout");
	    userId.setMaxAge(0);
	    response.addCookie(userId);
	}

}
