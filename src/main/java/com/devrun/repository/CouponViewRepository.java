package com.devrun.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devrun.dto.CouponListForStudent;
import com.devrun.dto.CouponListInCart;
import com.devrun.entity.CouponViewEntity;

@Repository
public interface CouponViewRepository extends JpaRepository<CouponViewEntity, Long> {

	CouponViewEntity findByCouponcode(String couponcode);

	List<CouponViewEntity> findAllByUserno(int userNo);
	
	List<CouponListInCart> findByUserno(int userNo);
	
	@Query(value = "SELECT lecturename, couponcode, discountrate , expirydate, issueddate, state, ROW_NUMBER() OVER() AS issuedno FROM coupon_manage WHERE userno = :userno ORDER BY lecturename ASC, issueddate DESC", nativeQuery = true)
	List<CouponListForStudent> findByUserno2(@Param("userno") int userNo);
}
