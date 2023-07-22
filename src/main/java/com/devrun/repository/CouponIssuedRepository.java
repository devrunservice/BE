package com.devrun.repository;

import com.devrun.entity.CouponIssued;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssuedRepository extends JpaRepository<CouponIssued, Long> {
	

}
