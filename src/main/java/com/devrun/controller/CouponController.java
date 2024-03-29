package com.devrun.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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

import com.devrun.dto.CouponIssuanceRequestDTO;
import com.devrun.dto.CouponListForMento;
import com.devrun.dto.CouponListForStudent;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.CouponViewRepository;
import com.devrun.service.CouponSerivce;
import com.devrun.service.MemberService;
import com.devrun.util.JWTUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@Api(tags = "쿠폰 API")
@RequiredArgsConstructor
public class CouponController {
	private final CouponSerivce couponSerivce;
	private final MemberService memberService;

	@GetMapping({ "/coupon/readmycoupon" })
	@ApiOperation(value = "쿠폰 조회하기", notes = "로그인 한 회원이 가진 쿠폰을 조회합니다.")
	public ResponseEntity<?> readmycoupon() {
		String userid = SecurityContextHolder.getContext().getAuthentication().getName();
		MemberEntity userEntity = memberService.findById(userid);

		List<CouponListForStudent> couponlist = couponSerivce.readmycoupon(userEntity);
		if (couponlist.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("보유한 쿠폰이 없습니다.");
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("content", couponlist);

		return ResponseEntity.ok(jsonObject);

	}

	@PostMapping("/coupon/publish")
	@ApiOperation(value = "쿠폰 생성", notes = "쿠폰을 생성하여 DB에 저장합니다.")
	public ResponseEntity<?> couponGeneration(@RequestBody @Valid CouponIssuanceRequestDTO couponIssuanceRequestDTO,
			HttpServletRequest request) {
		String userid = JWTUtil.getUserIdFromToken(request.getHeader("Access_token"));
		MemberEntity mentoEntity = memberService.findById(userid);
		CouponIssuanceRequestDTO issuedCoupone = couponSerivce.saveCouponDetail(couponIssuanceRequestDTO, mentoEntity);
		return ResponseEntity.ok().body(issuedCoupone);

	}

	@PostMapping("/coupon/registration")
	@ApiOperation(value = "쿠폰 등록", notes = "유저가 쿠폰 코드를 입력하면 쿠폰을 검증하고, 쿠폰을 등록합니다.")
	@ApiImplicitParam(name = "map", value = "등록할 쿠폰코드", example = "{\"couponcode\" : \"31267-ydi2OLCKFOaz\"}", required = true, dataTypeClass = Object.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "성공적으로 처리되었습니다"),
			@ApiResponse(code = 400, message = "옳지 않은 키 값입니다. 키 값 : couponcode") })
	public ResponseEntity<?> userGetCoupon(@RequestBody Map<String, String> map) {
		String couponCode = map.get("couponcode");
		if (couponCode.isEmpty()) {
			return ResponseEntity.ok("쿠폰코드가 입력되지 않았습니다.");
		}

		if (couponSerivce.validate(couponCode)) {
			String userid = SecurityContextHolder.getContext().getAuthentication().getName();
			String res = couponSerivce.checkcoupon(couponCode, userid);
			return ResponseEntity.status(HttpStatus.OK).body(res);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠폰 코드를 정확히 입력해주세요");
		}
	}

	@PostMapping("/coupon/shrewder")
	@ApiOperation(value = "쿠폰 파쇄기", notes = "특정 쿠폰을 사용 정지 처리하거나 복구합니다. 단건별로 처리합니다.")
	@ApiImplicitParam(name = "codelist", value = "사용 정지할 쿠폰 코드", example = "{\"code\" : \"31267-ydi2OLCKFOaz\"}", required = true, dataTypeClass = Object.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "성공적으로 처리되었습니다"),
			@ApiResponse(code = 400, message = "옳지 않은 키 값입니다. 키 값 : code") })
	public ResponseEntity<?> couponremove(@RequestBody Map<String, String> codelist) {
		String userid = SecurityContextHolder.getContext().getAuthentication().getName();
		MemberEntity userEntity = memberService.findById(userid);

		String targetcode = codelist.get("code");
		if (targetcode.isEmpty()) {
			return ResponseEntity.ok("쿠폰코드가 입력되지 않았습니다.");
		}
		String rsl = couponSerivce.removecode(userEntity, targetcode);
		return ResponseEntity.ok(rsl);
	}

	@GetMapping({ "/coupon/mento/couponmanaging", "/coupon/mento/couponmanaging/{pageno}" })
	@ApiOperation(value = "멘토가 발행한 쿠폰 조회", notes = "멘토가 발행한 쿠폰을 개별적으로 조회합니다.", response = CouponListForMento.class)
	@ApiImplicitParam(name = "pageno", value = "조회할 페이지", example = "1", required = false, dataTypeClass = Integer.class)
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
		jsonObject.put("totalElements", couponlist.getTotalElements());
		jsonObject.put("totalPages", couponlist.getTotalPages());
		jsonObject.put("content", couponlist.getContent());

		return ResponseEntity.ok(jsonObject);
	}
	
	@GetMapping("/coupon/mento/lecture")
	@ApiOperation(value = "멘토 강의 조회", notes = "멘토가 쿠폰을 발급하기 위해 자신이 만든 강의 리스트를 조회")
	public List<Map<String, String>> getMentoLecture(HttpServletRequest request){
		String userid = JWTUtil.getUserIdFromToken(request.getHeader("Access_token"));
		MemberEntity userEntity = memberService.findById(userid);
		return couponSerivce.findMentoLecture(userEntity);
		
	}

}
