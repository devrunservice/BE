package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MemberEntity;

public interface MemberEntityRepository extends JpaRepository<MemberEntity, Long> {

	int countById(String id);

	int countByEmail(String email);

	int countByPhonenumber(String phonenumber);

	MemberEntity findById(String id);

	String deleteById(String id);
}
