package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devrun.entity.PointEntity;

@Repository
public interface PointRepository extends JpaRepository<PointEntity, Long> {

	@Query("SELECT p FROM PointEntity p WHERE p.memberEntity.userNo = :userNo")
    PointEntity findByUserNo(@Param("userNo") int userNo);
	
//	PointEntity findByMemberEntity_UserNo(int userNo);
}