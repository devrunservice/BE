package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devrun.dto.member.MemberDTO;
import com.devrun.entity.MemberEntity;

@Repository
public interface LoginRepository extends JpaRepository<MemberEntity, Long> {

	MemberEntity findById(String id);

}
