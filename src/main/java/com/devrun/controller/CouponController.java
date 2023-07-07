package com.devrun.controller;


import com.devrun.entity.CouponIssued;
import com.devrun.entity.UserCoupon;
import com.devrun.service.CouponSerivce;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@Api(tags = "DevrunCoupon")
public class CouponController {


    @Autowired
    private CouponSerivce couponSerivce;

    @PostMapping("/coupon/makecoupon")
    @ResponseBody
    @ApiOperation("쿠폰을 생성하여 DB에 저장합니다.")
    public CouponIssued couponGeneration(@RequestBody CouponIssued couponBlueprint) {
        //타입오류 발생시 에러 처리 코드 필요
        couponSerivce.saveCouponDetail(couponBlueprint);
        return couponBlueprint;

    }

    @PostMapping("/coupon/registrate")
    @ResponseBody
    @ApiOperation("유저가 쿠폰 코드를 입력하면 쿠폰을 검증하고, 쿠폰을 획득합니다.")
    @ApiImplicitParam(name = "map"
            , value = "json타입 / 키값 : code => 쿠폰 코드 , 키값: id => 추후 헤더 토큰값으로 추출 예정(테스트용)"
            )
    public String userGetCoupon(@RequestBody Map<String , String> map){
        if(couponSerivce.validate(map.get("code"))){
            System.out.println("정확함");
            System.out.println("검증대상 : " + map.get("code"));
            String userid = map.get("id");
            String res =couponSerivce.checkcoupon(map.get("code"), userid);
            return res;
        } else {
            return "쿠폰 코드를 정확히 입력해주세요";
        }
    }

}
