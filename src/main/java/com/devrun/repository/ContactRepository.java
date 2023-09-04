package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.Consent;
import com.devrun.entity.Contact;
import com.devrun.entity.MemberEntity;

public interface ContactRepository extends JpaRepository<Contact, Long> {

	Contact findByMemberEntity(MemberEntity memberEntity);
}
