package com.devrun.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.CouponDTO;
import com.devrun.dto.CouponResponseDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.service.DiscountService;
import com.devrun.service.MemberService;

import io.swagger.annotations.ApiOperation;

@RestController
public class DiscountController {
    @Autowired
    private DiscountService discountService;
    @Autowired
    private MemberService memberService;

    @PostMapping("/applyCoupon")
    @ApiOperation("보유하고 있는 쿠폰을 적용합니다.")
    public ResponseEntity<?> applyCoupon(@RequestBody List<CouponDTO> couponDTOList) {
        System.err.println(couponDTOList);
        String userid = SecurityContextHolder.getContext().getAuthentication().getName();
        MemberEntity member = memberService.findById(userid);
        CouponResponseDTO couponResponseDTO = discountService.applyCoupons(couponDTOList, member);
        return ResponseEntity.ok(couponResponseDTO);        
    }    
}

