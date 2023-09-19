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
    	MemberEntity existingMember = loginRepository.findById(memberEntity.getId());
    	LoginInfo loginInfo = loginInfoRepository.findByMemberEntity(existingMember);
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
	
	public ResponseCookie setRefeshCookie(String token) {
		
		String value = "Bearer " + token;
		String encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
		ResponseCookie refresh_token = ResponseCookie
			.from("Refresh_token", encodedValue)
//			.domain("devrun.site")
			.path("/authz")
			.sameSite("none")
			// 로컬 테스트를 위해 false
//			.secure(true)
//			.httpOnly(true)
			.secure(false)
			.httpOnly(false)
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
