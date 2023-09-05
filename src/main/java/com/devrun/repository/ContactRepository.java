package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devrun.entity.Contact;
import com.devrun.entity.MemberEntity;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

	Contact findByMemberEntity(MemberEntity memberEntity);
	
	int countByEmail(String email);

	int countByPhonenumber(String phonenumber);
}
