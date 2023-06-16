package com.devrun.service;

import org.springframework.stereotype.Service;

import com.devrun.entity.MemberEntity;
import com.devrun.repository.MemberEntityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignupService {

	private final MemberEntityRepository memberEntityRepository;
	
	public void insert(MemberEntity memberEntity) {
		memberEntityRepository.save(memberEntity);
	}

}
