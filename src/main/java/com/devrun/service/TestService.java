package com.devrun.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devrun.entity.MemberEntity;
import com.devrun.repository.MemberEntityRepository;

@Service
public class TestService {
	
	@Autowired
	private MemberEntityRepository memberEntityRepository;

	public List<MemberEntity> findAll() {
		return memberEntityRepository.findAll();
	}

//	TransactionRequiredException이 발생하면 JPA가 데이터베이스 트랜잭션 내에서 동작해야 한다는 것을 의미합니다.
//	이를 해결하기 위해 Service 레이어의 메소드에 @Transactional 어노테이션을 추가해주시기 바랍니다.
//	@Transactional 어노테이션을 사용하면 해당 메소드가 트랜잭션 내에서 실행됩니다. 트랜잭션이 없으면 새로운 트랜잭션을 시작하고, 메소드 실행이 끝나면 트랜잭션을 커밋하거나 롤백합니다.
	@Transactional
	public String deleteId(String id) {
		return memberEntityRepository.deleteById(id);
	}

}
