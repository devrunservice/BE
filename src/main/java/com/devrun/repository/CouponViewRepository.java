package com.devrun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.devrun.entity.CouponViewEntity;

@Repository
public interface CouponViewRepository extends JpaRepository<CouponViewEntity, Long> {

	CouponViewEntity findByCouponcode(String couponcode);

	List<CouponViewEntity> findAllByUserno(int userNo);
	
//	@Query(value = "select * from coupon_manage cm WHERE cm.userno = :userno" , nativeQuery = true)
//	List<CouponViewEntity> activatequery(@Param("userno") int userNo);



}
