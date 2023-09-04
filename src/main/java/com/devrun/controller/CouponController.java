package com.devrun.controller;


import com.devrun.entity.CouponIssued;
import com.devrun.entity.CouponViewEntity;
import com.devrun.entity.Couponregicode;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.CouponViewRepository;
import com.devrun.repository.PaymentInfo;
import com.devrun.service.CouponSerivce;
import com.devrun.service.MemberService;
import com.devrun.util.JWTUtil;

import io.swagger.annotations.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
public class CouponController {

    @Autowired
    private CouponSerivce couponSerivce;
    
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private CouponViewRepository couponViewRepository;
    
    
    @GetMapping("/coupon/readmycoupon")
    @ApiOperation(value = "쿠폰 조회하기" , notes = "로그인 한 회원이 가진 쿠폰을 조회합니다.")
    public ResponseEntity<?> readmycoupon(){
    	System.out.println("---------------------------------CouponController readmycoupon method start---------------------------------");
    	String userid = SecurityContextHolder.getContext().getAuthentication().getName();
    	MemberEntity userEntity = memberService.findById(userid);
    	List<CouponViewEntity> couponlist = couponSerivce.readmycoupon(userEntity);
    	if(couponlist.isEmpty()) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("보유한 쿠폰이 없습니다.");
    	}
		return ResponseEntity.ok(couponlist);
    	
    	
    }

    @PostMapping("/coupon/publish")
    @ApiOperation(value = "쿠폰 생성기", notes = "쿠폰을 생성하여 DB에 저장합니다.")
    @ApiImplicitParam(name = "couponBlueprint" , value = "생성할 쿠폰 디테일" , required = true , dataType = "CouponIssued")
    public ResponseEntity<?> couponGeneration(@RequestBody @Valid CouponIssued couponBlueprint) {
//    	String userid = SecurityContextHolder.getContext().getAuthentication().getName();
//    	MemberEntity userEntity = memberService.findById(userid);
//    	강의 도메인 완성되면 멘토가 개설한 강의에 대한 쿠폰만 만들 수 있어도록 검증 과정 필요함
    	
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
    public ResponseEntity<?> userGetCoupon(@RequestBody Map<String,String> map){
    	String couponCode = map.get("couponcode");
    	
        if(couponSerivce.validate(couponCode)){
            String userid = SecurityContextHolder.getContext().getAuthentication().getName();
            String res = couponSerivce.checkcoupon(couponCode, userid);
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠폰 코드를 정확히 입력해주세요");
        }
    }

    @PostMapping("/coupon/shrewder")
    @ApiOperation(value = "쿠폰 파쇄기", notes = "특정 쿠폰을 사용 정지 처리하거나 복구합니다. 다중선택이 가능합니다.")
    @ApiImplicitParam(name = "codelist" , value = "사용 정지할 쿠폰 코드" , required = true , dataType = "Map<String , List<String>>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 처리되었습니다"),
            @ApiResponse(code = 400, message = "옳지 않은 키 값입니다. 키 값 : code")
    })
    public ResponseEntity<?> couponremove(@RequestBody Map<String , List<String>> codelist){
    	String userid = SecurityContextHolder.getContext().getAuthentication().getName();
    	MemberEntity userEntity = memberService.findById(userid);
    	
    	List<String> targetcodelist = codelist.get("codelist");
    	if(targetcodelist.isEmpty()) {
    		return ResponseEntity.ok("쿠폰코드가 입력되지 않았습니다.");
    	}
        String rsl = couponSerivce.removecode(userEntity,targetcodelist);        
        return ResponseEntity.ok(rsl);
    }
    
    
    @GetMapping("/coupon/mento/couponmanaging")
    @ApiOperation(value = "멘토가 발행한 쿠폰 조회", notes = "멘토가 발행한 쿠폰을 개별적으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 처리되었습니다")            
    })
    public ResponseEntity<?> couponmanagingByMento(){
    	String userid = SecurityContextHolder.getContext().getAuthentication().getName();
    	MemberEntity userEntity = memberService.findById(userid);    	
    	List<Couponregicode> couponlist = couponSerivce.readCouponMadeByMento(userEntity);
    	
    	if(couponlist.isEmpty()) {
    		return ResponseEntity.ok("발행한 쿠폰이 없습니다.");
    	}
    	
    	return ResponseEntity.ok(couponlist);   	
    }
    
    
}
