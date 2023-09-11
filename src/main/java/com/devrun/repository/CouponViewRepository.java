package com.devrun.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devrun.dto.CouponListForStudent;
import com.devrun.entity.CouponViewEntity;

@Repository
public interface CouponViewRepository extends JpaRepository<CouponViewEntity, Long> {

	CouponViewEntity findByCouponcode(String couponcode);

	List<CouponViewEntity> findAllByUserno(int userNo);
	
	@Query(value = "SELECT d.target AS lecturename, d.couponcode AS couponcode, d.discountrate AS discountrate , d.expirydate AS expirydate, d.issueddate AS issueddate, d.state AS state, ROW_NUMBER() OVER() AS issuedno FROM coupon_manage d WHERE d.userno = :userno ORDER BY target ASC, issueddate DESC", nativeQuery = true)
	List<CouponListForStudent> findAllByUserno2(@Param("userno") int userNo);



}
