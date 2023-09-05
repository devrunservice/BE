package com.devrun.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.CouponListForMento;
import com.devrun.entity.CouponIssued;
import com.devrun.entity.CouponViewEntity;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.CouponViewRepository;
import com.devrun.service.CouponSerivce;
import com.devrun.service.MemberService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class CouponController {

	@Autowired
	private CouponSerivce couponSerivce;

	@Autowired
	private MemberService memberService;

	@Autowired
	private CouponViewRepository couponViewRepository;

	@GetMapping({"/coupon/readmycoupon","/coupon/readmycoupon/{pageno}"})
	@ApiOperation(value = "쿠폰 조회하기", notes = "로그인 한 회원이 가진 쿠폰을 조회합니다.")
	@ApiImplicitParam(name = "pageno", value = "조회할 페이지", dataType = "Number")
	public ResponseEntity<?> readmycoupon(@PathVariable(required = false) Integer pageno) {
		System.out.println(
				"---------------------------------CouponController readmycoupon method start---------------------------------");

		if (pageno == null || pageno <= 0) {
			pageno = 1;
		}
		int size = 10;
		Pageable pageable = PageRequest.of(pageno - 1, size);

		String userid = SecurityContextHolder.getContext().getAuthentication().getName();
		MemberEntity userEntity = memberService.findById(userid);
		
		Page<CouponViewEntity> couponlist = couponSerivce.readmycoupon(userEntity, pageable);
		if (couponlist.getTotalElements() <= 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("보유한 쿠폰이 없습니다.");
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("totalelements", couponlist.getTotalElements());
		jsonObject.put("totalpages", couponlist.getTotalPages());
		jsonObject.put("couponlist", couponlist.getContent());
		
		return ResponseEntity.ok(jsonObject);

	}

	@PostMapping("/coupon/publish")
	@ApiOperation(value = "쿠폰 생성기", notes = "쿠폰을 생성하여 DB에 저장합니다.")
	@ApiImplicitParam(name = "couponBlueprint", value = "생성할 쿠폰 디테일", required = true, dataType = "CouponIssued")
	public ResponseEntity<?> couponGeneration(@RequestBody @Valid CouponIssued couponBlueprint) {
//    	String userid = SecurityContextHolder.getContext().getAuthentication().getName();
//    	MemberEntity userEntity = memberService.findById(userid);
//    	강의 도메인 완성되면 멘토가 개설한 강의에 대한 쿠폰만 만들 수 있어도록 검증 과정 필요함

		couponSerivce.saveCouponDetail(couponBlueprint);
		return ResponseEntity.ok().body("저장 완료");

	}

	@PostMapping("/coupon/registration")
	@ApiOperation(value = "쿠폰 등록기", notes = "유저가 쿠폰 코드를 입력하면 쿠폰을 검증하고, 쿠폰을 등록합니다.")
	@ApiImplicitParam(name = "map", value = "등록할 쿠폰코드", required = true)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "성공적으로 처리되었습니다"),
			@ApiResponse(code = 400, message = "옳지 않은 키 값입니다. 키 값 : couponcode") })
	public ResponseEntity<?> userGetCoupon(@RequestBody Map<String, String> map) {
		String couponCode = map.get("couponcode");

		if (couponSerivce.validate(couponCode)) {
			String userid = SecurityContextHolder.getContext().getAuthentication().getName();
			String res = couponSerivce.checkcoupon(couponCode, userid);
			return ResponseEntity.status(HttpStatus.OK).body(res);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠폰 코드를 정확히 입력해주세요");
		}
	}

	@PostMapping("/coupon/shrewder")
	@ApiOperation(value = "쿠폰 파쇄기", notes = "특정 쿠폰을 사용 정지 처리하거나 복구합니다. 다중선택이 가능합니다.")
	@ApiImplicitParam(name = "codelist", value = "사용 정지할 쿠폰 코드", required = true, dataType = "Map<String , List<String>>")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "성공적으로 처리되었습니다"),
			@ApiResponse(code = 400, message = "옳지 않은 키 값입니다. 키 값 : code") })
	public ResponseEntity<?> couponremove(@RequestBody Map<String, List<String>> codelist) {
		String userid = SecurityContextHolder.getContext().getAuthentication().getName();
		MemberEntity userEntity = memberService.findById(userid);

		List<String> targetcodelist = codelist.get("codelist");
		if (targetcodelist.isEmpty()) {
			return ResponseEntity.ok("쿠폰코드가 입력되지 않았습니다.");
		}
		String rsl = couponSerivce.removecode(userEntity, targetcodelist);
		return ResponseEntity.ok(rsl);
	}

	@GetMapping({"/coupon/mento/couponmanaging" , "/coupon/mento/couponmanaging/{pageno}"})
	@ApiOperation(value = "멘토가 발행한 쿠폰 조회", notes = "멘토가 발행한 쿠폰을 개별적으로 조회합니다.", response = CouponListForMento.class)
	@ApiImplicitParam(name = "pageno", value = "조회할 페이지", dataType = "Number")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "성공적으로 처리되었습니다") })
	public ResponseEntity<?> couponmanagingByMento(@PathVariable(required = false) Integer pageno) {
		if (pageno == null || pageno <= 0) {
			pageno = 1;
		}
		String userid = SecurityContextHolder.getContext().getAuthentication().getName();
		MemberEntity userEntity = memberService.findById(userid);
		int size = 10;
		Pageable pageable = PageRequest.of(pageno - 1, size);
		Page<CouponListForMento> couponlist = couponSerivce.readCouponMadeByMento(userEntity, pageable);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("totalelements", couponlist.getTotalElements());
		jsonObject.put("totalpages", couponlist.getTotalPages());
		jsonObject.put("couponlist", couponlist.getContent());
		
		return ResponseEntity.ok(jsonObject);
	}

}
