package com.devrun.controller;


import com.devrun.entity.CouponIssued;
import com.devrun.entity.UserCoupon;
import com.devrun.service.CouponSerivce;
import io.swagger.annotations.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class CouponController {

    @Autowired
    private CouponSerivce couponSerivce;

    @PostMapping("/coupon/publish")
    @ApiOperation(value = "쿠폰 생성기", notes = "쿠폰을 생성하여 DB에 저장합니다.")
    public ResponseEntity<?> couponGeneration(@RequestBody CouponIssued couponBlueprint) {
        //타입오류 발생시 에러 처리 코드 필요
        couponSerivce.saveCouponDetail(couponBlueprint);
        return ResponseEntity.ok().body("저장 완료");

    }

    @PostMapping("/coupon/registration")
    @ApiOperation(value = "쿠폰 등록기", notes = "유저가 쿠폰 코드를 입력하면 쿠폰을 검증하고, 쿠폰을 등록합니다.")
    @ApiImplicitParam(name = "map" , value = "등록할 쿠폰코드" , required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 처리되었습니다"),
            @ApiResponse(code = 400, message = "옳지 않은 키 값입니다. 키 값 : couponcode")
            })
    public String userGetCoupon(@RequestBody Map<String,String> map){
    	String couponCode = map.get("couponcode");
    	
        if(couponSerivce.validate(couponCode)){
            String userid = SecurityContextHolder.getContext().getAuthentication().getName();
            String res = couponSerivce.checkcoupon(couponCode, userid);
            return res;
        } else {
            return "쿠폰 코드를 정확히 입력해주세요";
        }
    }

    @PostMapping("/coupon/shrewder")
    @ApiOperation(value = "쿠폰 파쇄기", notes = "특정 쿠폰을 사용 정지 처리하거나 복구합니다.")
    @ApiImplicitParam(name = "map" , value = "사용 정지할 쿠폰 코드" , required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 처리되었습니다"),
            @ApiResponse(code = 400, message = "옳지 않은 키 값입니다. 키 값 : code")
    })
    public ResponseEntity couponremove(@RequestBody Map<String , String> map){
        String targetcode = map.get("code");
        try{
                int able = Integer.valueOf(map.get("able"));
                if(able > 1){
                    able = 1;
                }
            String res = couponSerivce.removecode(targetcode , able);
            return ResponseEntity.ok().body(res);

    } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("only number allowed");
        }
    }
}
