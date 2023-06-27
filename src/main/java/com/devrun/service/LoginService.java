package com.devrun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devrun.dto.member.LoginDTO.LoginStatus;
import com.devrun.dto.member.MemberDTO.Status;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.LoginRepository;
import com.devrun.repository.MemberEntityRepository;

@Service
public class LoginService {
	
	@Autowired
	private LoginRepository loginRepository;
	
	private MemberEntityRepository memberEntityRepository;
	
	private LoginStatus loginStatus;

	// 마지막 로그인 날짜 수정
	public void save(MemberEntity memberEntity) {
		memberEntityRepository.save(memberEntity);
	}
	public LoginStatus validate(MemberEntity member) {
		MemberEntity existingMember = loginRepository.findById(member.getId());
		
		if (existingMember == null) {
		    return LoginStatus.USER_NOT_FOUND;
		} else if (existingMember.getLoginTry() >= 5) {
		    return LoginStatus.LOGIN_TRIES_EXCEEDED;
		} else if (!existingMember.getPassword().equals(member.getPassword())) {
		    existingMember.setLoginTry(existingMember.getLoginTry() + 1);
		    memberEntityRepository.save(existingMember);
		    return LoginStatus.PASSWORD_MISMATCH;
		} else if (existingMember.getStatus() == Status.INACTIVE) {
		    return LoginStatus.ACCOUNT_INACTIVE;
		} else if (existingMember.getStatus() == Status.WITHDRAWN) {
		    return LoginStatus.ACCOUNT_WITHDRAWN;
		} else {
		    existingMember.setLoginTry(0); // reset login tries on successful login
		    memberEntityRepository.save(existingMember);
		    return LoginStatus.SUCCESS;
		}
	}	

}
