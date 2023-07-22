package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devrun.entity.MemberEntity;

@Repository
public interface LoginRepository extends JpaRepository<MemberEntity, Long> {

	MemberEntity findById(String id);

	MemberEntity findByKakaoEmailId(String kakaoEmailId);
	
	@Query("SELECT m.id FROM MemberEntity m WHERE m.phonenumber = :phonenumber")
	String findByPhonenumber(@Param("phonenumber") String phonenumber);

}
