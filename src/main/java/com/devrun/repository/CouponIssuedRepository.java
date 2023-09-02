package com.devrun.repository;

import com.devrun.entity.CouponIssued;
import com.devrun.entity.MemberEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssuedRepository extends JpaRepository<CouponIssued, Long> {

	List<CouponIssued> findAllByIssueduser(MemberEntity userEntity);
	

}
