package com.devrun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.entity.CouponIssued;
import com.devrun.repository.CouponIssuedRepository;

@RestController
public class DiscountController {
	@Autowired
	private CouponIssuedRepository CouponIssuedRepository;

	@PostMapping("/applyCoupon")
	public ResponseEntity<?> applyCoupon(@RequestParam("CouponCode") String couponcode,
			@RequestParam("amount") int amount) {
		
		// 쿠폰 코드를 사용하여 할인율 조회
		CouponIssued coupon = CouponIssuedRepository.findByCouponCode(couponcode);
		int discountRate = coupon.getDiscountrate();
		// 할인된 결제 금액 계산
		int discountedAmount = (int) (amount * (1 - (discountRate / 100.0)));

		// 할인된 결제 금액을 응답으로 반환
		return ResponseEntity.ok(discountedAmount);
	}

}
