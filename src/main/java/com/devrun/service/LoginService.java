package com.devrun.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devrun.dto.member.LoginDTO.LoginStatus;
import com.devrun.dto.member.MemberDTO;
import com.devrun.dto.member.MemberDTO.Status;
import com.devrun.repository.LoginRepository;
import com.devrun.repository.MemberEntityRepository;

@Service
public class LoginService {
	
	@Autowired
	private LoginRepository loginRepository;
	
	@Autowired
	private MemberEntityRepository memberEntityRepository;
	
	private LoginStatus loginStatus;

	// 마지막 로그인 날짜 수정
	public void setLastLogin(MemberDTO memberDTO) {
		Date currentDate = new Date();
    	System.out.println("현재시간 : " + currentDate);
    	memberDTO.setLastlogin(currentDate);
		memberEntityRepository.save(memberDTO);
	}
	public LoginStatus validate(MemberDTO memberDTO) {
		MemberDTO existingMember = loginRepository.findById(memberDTO.getId());
		System.out.println(existingMember);
		
		if (existingMember == null) {
		    return LoginStatus.USER_NOT_FOUND;
		} else if (existingMember.getLogintry() >= 5) {
		    return LoginStatus.LOGIN_TRIES_EXCEEDED;
		} else if (!existingMember.getPassword().equals(memberDTO.getPassword())) {
		    existingMember.setLogintry(existingMember.getLogintry() + 1);
		    memberEntityRepository.save(existingMember);
		    return LoginStatus.PASSWORD_MISMATCH;
		} else if (existingMember.getStatus() == Status.INACTIVE) {
		    return LoginStatus.ACCOUNT_INACTIVE;
		} else if (existingMember.getStatus() == Status.WITHDRAWN) {
		    return LoginStatus.ACCOUNT_WITHDRAWN;
		} else {
		    existingMember.setLogintry(0); // reset login tries on successful login
		    memberEntityRepository.save(existingMember);
		    return LoginStatus.SUCCESS;
		}
	}	

}
