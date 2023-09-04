package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MemberEntity;
import com.devrun.entity.PointEntity;

public interface MemberEntityRepository extends JpaRepository<MemberEntity, Long> {

	int countById(String id);

	int countByEmail(String email);

	int countByPhonenumber(String phonenumber);

	MemberEntity findById(String id);

	String deleteById(String id);

	PointEntity save(PointEntity point);

	MemberEntity findByName(String name);
	
}
