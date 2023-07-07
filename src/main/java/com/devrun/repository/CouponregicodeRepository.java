package com.devrun.repository;

import com.devrun.entity.Couponregicode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponregicodeRepository extends JpaRepository<Couponregicode , Long> {

    int countByCouponcode(String code);

    @Procedure("check_coupon_status")
    String getCouponStatus(String code, String user);
}
