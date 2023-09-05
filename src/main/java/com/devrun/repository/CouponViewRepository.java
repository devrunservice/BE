package com.devrun.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.devrun.entity.CouponViewEntity;

@Repository
public interface CouponViewRepository extends JpaRepository<CouponViewEntity, Long> {

	CouponViewEntity findByCouponcode(String couponcode);

	List<CouponViewEntity> findAllByUserno(int userNo);
	
	@Query(value = "SELECT d.userno AS userno , d.target AS target, d.couponcode AS couponcode, d.discountrate AS discountrate , d.expirydate AS expirydate, d.issueddate AS issueddate, d.quantity AS quantity , d.state AS state, ROW_NUMBER() OVER(ORDER BY target ASC , issueddate DESC) AS issuedno FROM coupon_manage d WHERE d.userno = :userno ORDER BY target ASC, issueddate DESC", nativeQuery = true)
	Page<CouponViewEntity> findAllByUserno2(@Param("userno") int userNo, Pageable pageable);



}
