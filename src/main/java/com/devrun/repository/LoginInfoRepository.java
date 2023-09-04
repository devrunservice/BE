package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.LoginInfo;
import com.devrun.entity.MemberEntity;

public interface LoginInfoRepository extends JpaRepository<LoginInfo, Long> {

	LoginInfo findByMemberEntity(MemberEntity memberEntity);

}
