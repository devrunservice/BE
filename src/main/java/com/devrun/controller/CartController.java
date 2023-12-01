package com.devrun.controller;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.CouponListInCart;
import com.devrun.dto.LectureInfo;
import com.devrun.entity.MemberEntity;
import com.devrun.service.CartService;
import com.devrun.service.MemberService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@Api(tags = "장바구니 API")
@RequiredArgsConstructor
public class CartController {
	private final CartService cartService;
	private final MemberService memberService;

	@PostMapping("/cart/insert")
	@ApiOperation(value = "장바구니에 강의를 추가합니다." , notes = "이미 구입한 강의일 경우 알림 메세지를 반환합니다.")
	@ApiImplicitParam(name = "lectureId", value = "강의 식별 번호", example = "22", dataTypeClass = Long.class)
	public ResponseEntity<?> putCart(@RequestBody(required = true) Long lectureId) {
		String userid = SecurityContextHolder.getContext().getAuthentication().getName();
		MemberEntity userEntity = memberService.findById(userid);
		String msg = cartService.putInCart(userEntity, lectureId);
		return ResponseEntity.ok(msg);
	}

	@PostMapping("/cart/delete")
	@ApiOperation(value = "장바구니에서 강의를 삭제합니다." , notes = "lectureId 가 아닌 cartId 배열을 요청하면 강의를 삭제 처리합니다.")
	@ApiImplicitParam(name = "cartId", value = "장바구니 식별 번호 배열", example = "[1,2]", dataTypeClass = Long.class)
	public String deleteInCart(@RequestBody(required = true) List<Long> cartId) {
		String userid = SecurityContextHolder.getContext().getAuthentication().getName();
		MemberEntity userEntity = memberService.findById(userid);
		cartService.deleteInCart(userEntity, cartId);
		String msg = "처리 완료";
		return msg;
	}

	@GetMapping("/cart")
	@ApiOperation("장바구니 화면에 출력할 모든 데이터를 전달합니다.")
	public ResponseEntity<?> cartopen() {
		String userid = SecurityContextHolder.getContext().getAuthentication().getName();
		MemberEntity userEntity = memberService.findById(userid);
		List<LectureInfo> lectureInfoList = cartService.showlectureInfo(userEntity);
		Map<String, Object> buyerInfo = cartService.showBuyerInfo(userEntity);
		List<CouponListInCart> couponListInCart = cartService.showUserCoupon(userEntity);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("lectureInfoList", lectureInfoList);
		jsonObject.put("buyerInfo", buyerInfo);
		jsonObject.put("couponListInCart", couponListInCart);

		return ResponseEntity.ok().body(jsonObject);
	}

	@GetMapping("/cart/list")
	@ApiOperation("장바구니 화면에 출력할 강의리스트와 쿠폰데이터를 전달합니다.")
	public ResponseEntity<?> cartlistopen() {
		String userid = SecurityContextHolder.getContext().getAuthentication().getName();
		MemberEntity userEntity = memberService.findById(userid);
		List<LectureInfo> lectureInfoList = cartService.showlectureInfo(userEntity);
		List<CouponListInCart> couponListInCart = cartService.showUserCoupon(userEntity);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("lectureInfoList", lectureInfoList);
		jsonObject.put("couponListInCart", couponListInCart);

		return ResponseEntity.ok().body(jsonObject);
	}

}
