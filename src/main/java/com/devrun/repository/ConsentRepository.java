package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.Consent;
import com.devrun.entity.MemberEntity;

public interface ConsentRepository extends JpaRepository<Consent, Long> {

	Consent findByMemberEntity(MemberEntity memberEntity);
}
