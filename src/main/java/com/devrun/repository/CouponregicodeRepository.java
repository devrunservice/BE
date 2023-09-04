package com.devrun.repository;

import com.devrun.entity.CouponIssued;
import com.devrun.entity.Couponregicode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.transaction.Transactional;

@Repository
public interface CouponregicodeRepository extends JpaRepository<Couponregicode , Long> {

    int countByCouponcode(String code);

    @Procedure("coupon_register_process")
    @Transactional
    String getCouponStatus(String code, String user);

    @Procedure("coupon_remove_recover")
    String removecode(String removecoupon , String able);

	List<Couponregicode> findAllByIssuedno(CouponIssued couponIssued);

}
