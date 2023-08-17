package com.devrun.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.CouponDTO;
import com.devrun.entity.CouponViewEntity;
import com.devrun.repository.CouponViewRepository;

import io.swagger.annotations.ApiOperation;


@RestController
public class DiscountController {
	@Autowired
	private CouponViewRepository couponViewRepository;

	@PostMapping("/applyCoupon")
    @ApiOperation("보유하고 있는 쿠폰을 적용합니다.")
	public ResponseEntity<?> applyCoupon(@RequestBody CouponDTO couponDTO) {
		System.err.println(couponDTO);
		
	    String couponcode = couponDTO.getCouponCode();
	    int amount = couponDTO.getamount();
	    
	   System.err.println(couponcode);
	   System.err.println(amount);
		
		// 쿠폰 코드를 사용하여 할인율 조회
		CouponViewEntity coupon = couponViewRepository.findByCouponcode(couponcode);
		System.err.println(coupon);
		
		int discountRate = coupon.getDiscountrate();		
		System.err.println(discountRate);
		
				// 할인된 결제 금액 계산
		int discountedAmount = (int) (amount * (1 - (discountRate / 100.0)));

		// 할인된 결제 금액을 응답으로 반환
		return ResponseEntity.ok(discountedAmount);
	}

}
