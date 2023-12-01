package com.devrun.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.MylectureReviewDTO;
import com.devrun.dto.ReviewRequest;
import com.devrun.entity.MemberEntity;
import com.devrun.service.MemberService;
import com.devrun.service.MylectureReviewService;
import com.devrun.util.JWTUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Api(tags = "수강평 API")
public class LectureReviewController {

	private final MemberService memberService;
	private final MylectureReviewService reviewService;

	@PostMapping("/reviewrating")
	@ApiOperation(value = "학습 후기 작성하고 평점 남기기", notes = "파라미터로 액세스 토큰과 강의 후기 내용(reviewContent), 강의 평점(reviewRating)를 제출합니다.")
	public String reviewProcess(HttpServletRequest httpServletRequest,
			@RequestBody(required = true) ReviewRequest reviewRequest) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);
		reviewService.saveReview(userEntity, reviewRequest);
		return "작성 완료";
	}

	@GetMapping("/review/{lectureId}/{pageNumber}")
	@ApiOperation(value = "해당 강의에 대한 수강평 보기", notes = "파라미터로 lectureId를 요청하면 해당 강의의 수강평을 반환합니다.")
	public List<MylectureReviewDTO> reviewList(@PathVariable Long lectureId, @PathVariable int pageNumber) {
		return reviewService.reviewList(lectureId, pageNumber);
	}

}
