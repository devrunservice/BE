package com.devrun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devrun.entity.MemberEntity;
import com.devrun.repository.LoginRepository;

@Service
public class LoginService {
	
	@Autowired
	private LoginRepository loginRepository;

	public boolean validate(MemberEntity member) {
		 MemberEntity existingMember = loginRepository.findById(member.getId());
		 if (existingMember != null) {
			 return existingMember.getPassword().equals(member.getPassword());
		}else {
			return false;
		}
	}

}
