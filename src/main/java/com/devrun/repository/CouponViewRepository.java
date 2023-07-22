package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.devrun.entity.CouponViewEntity;

@Repository
public interface CouponViewRepository extends JpaRepository<CouponViewEntity, Long> {

	CouponViewEntity findByCouponcode(String couponcode);


}
