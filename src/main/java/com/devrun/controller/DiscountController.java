package com.devrun.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.CouponDTO;
import com.devrun.entity.CouponViewEntity;
import com.devrun.entity.CouponViewEntity.couponstate;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.CouponViewRepository;
import com.devrun.service.MemberService;

import io.swagger.annotations.ApiOperation;


@RestController
public class DiscountController {
	@Autowired
	private CouponViewRepository couponViewRepository;
	@Autowired
	private MemberService memberService;

	@PostMapping("/applyCoupon")
	@ApiOperation("보유하고 있는 쿠폰을 적용합니다.")
	public ResponseEntity<?> applyCoupon(@RequestBody CouponDTO couponDTO) {
	    System.err.println(couponDTO);
	    
	    String userid = SecurityContextHolder.getContext().getAuthentication().getName();
	    MemberEntity member = memberService.findById(userid);
	    int usrno = member.getUserNo(); // name 대신 usrno로 변경
	    
	    String couponcode = couponDTO.getCouponCode();
	    int amount = couponDTO.getamount(); // 변수명을 amount로 변경
	    System.err.println(usrno);
	    System.err.println(couponcode);
	    System.err.println(amount);
	    
	    // 쿠폰코드 조회 
	    CouponViewEntity coupon = couponViewRepository.findByCouponcode(couponcode);
	    System.err.println(coupon);
	    
	    //1. 쿠폰 존재 확인
	    if (coupon != null) {
	    	couponstate state= coupon.getState();
	        System.err.println(state);
	        //2. 사용자가 보유한 쿠폰인지 확인
	        if (coupon.getUserno() == usrno) {
	        	//couponstate를 viewentity에다 만들어서 직접 호출
	        	//3. 쿠폰 상태 검증
	            if (couponstate.ACTIVE.equals(state)) {
	                int discountRate = coupon.getDiscountrate();
	                System.err.println(discountRate);
	    
	                // 할인된 결제 금액 계산
	                int discountedAmount = (int) (amount * (1 - (discountRate / 100.0)));
	    
	                // 할인된 결제 금액을 응답으로 반환
	                return ResponseEntity.ok(discountedAmount);
	            } else if (couponstate.EXPIRY.equals(state)) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠폰이 만료되었습니다.");
	            } else if (couponstate.REMOVED.equals(state)) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠폰이 삭제되었습니다.");
	            } else {
	            	// 나머지 상황
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠폰 상태가 유효하지 않습니다.");
	            }
	        } else {
	            // 쿠폰의 usrno가 로그인한 사용자와 일치하지 않는 경우
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자가 보유한 쿠폰이 아닙니다.");
	        }
	    } else {
	    	//쿠폰을 못 찾는 경우	
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠폰을 찾을 수 없습니다");
	    }
	}
}
