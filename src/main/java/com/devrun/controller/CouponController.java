package com.devrun.controller;


import com.devrun.entity.CouponIssued;
import com.devrun.entity.UserCoupon;
import com.devrun.service.CouponSerivce;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@Api(tags = "DevrunCoupon")
public class CouponController {

    @Autowired
    private CouponSerivce couponSerivce;

    @PostMapping("/coupon/publish")
    @ApiOperation("쿠폰을 생성하여 DB에 저장합니다.")
    public ResponseEntity<?> couponGeneration(@RequestBody CouponIssued couponBlueprint) {
        //타입오류 발생시 에러 처리 코드 필요
        couponSerivce.saveCouponDetail(couponBlueprint);
        return ResponseEntity.ok().body("저장 완료");

    }

    @PostMapping("/coupon/registration")
    @ApiOperation("유저가 쿠폰 코드를 입력하면 쿠폰을 검증하고, 쿠폰을 등록합니다.")
    @ApiImplicitParam(name = "couponecode"
            , value = "쿠폰 코드")
    public String userGetCoupon(@RequestBody String couponecode){
        if(couponSerivce.validate(couponecode)){
            String userid = SecurityContextHolder.getContext().getAuthentication().getName();
            String res = couponSerivce.checkcoupon(couponecode, userid);
            return res;
        } else {
            return "쿠폰 코드를 정확히 입력해주세요";
        }
    }

    @PostMapping("/coupon/shrewder")
    @ApiOperation("관리자 계정으로, 특정 쿠폰을 사용 정지 처리하거나 복구합니다.")
    @ApiImplicitParam(name = "map"
            , value = "json타입 / 키값 : code => 쿠폰 코드 , 키값: able => 1(사용 정지) or 0(복구)"
    )
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
