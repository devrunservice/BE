package com.devrun.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.LectureOfMentoDtos;
import com.devrun.dto.QueryLectureByKeywordDTO2;
import com.devrun.entity.MemberEntity;
import com.devrun.service.MemberService;
import com.devrun.util.JWTUtil;
import com.devrun.youtube.LectureService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Api(tags = "강의 검색 API")
public class LectureSearchController {
	private final MemberService memberService;
	private final LectureService lectureService;

	@GetMapping({"/q/lecture" , "/q/lecture"})
	@ApiImplicitParams({
			@ApiImplicitParam(example = "요리", value = "대분류 카테고리", name = "bigcategory", dataTypeClass = String.class),
			@ApiImplicitParam(example = "라면", value = "중분류 카테고리", name = "midcategory", dataTypeClass = String.class),
			@ApiImplicitParam(example = "sky", value = "검색 키워드", name = "q", dataTypeClass = String.class),
			@ApiImplicitParam(example = "lecture_start", value = "정렬 옵션", name = "order", dataTypeClass = String.class),
			@ApiImplicitParam(example = "1", value = "요청 페이지", name = "page", dataTypeClass = String.class) })
	@ApiOperation(value = "강의 조회 API", notes = "파라미터로 키워드를 입력하면 강의를 반환합니다. 각 파라미터로 키워드, 정렬 옵션, 페이지 를 요청할 수 있고, 각 페이지 당 10개의 항목이 반환됩니다. 정렬 옵션은 lecture_start (등록날짜순) , lecture_price (가격순) , buy_count (구매자순)이며 추후 제약 조건들을 추가하고, 평점 기능이 도입되면 평점순도 추가할 예정입니다. 정렬 옵션을 입력하지 않으면 기본적으론 등록순이며 모든 정렬은 내림차순입니다.")
	public QueryLectureByKeywordDTO2 getLectureSearch(
			HttpServletRequest httpServletRequest,
			@RequestParam(value = "bigcategory", defaultValue = "", required = false) String bigcategory,
			@RequestParam(value = "midcategory", defaultValue = "", required = false) String midcategory,
			@RequestParam(value = "q", defaultValue = "", required = false) String keyword,
			@RequestParam(value = "order", defaultValue = "lecture_start", required = false) String order,
			@RequestParam(value = "page", defaultValue = "0", required = false) Integer page) {
		if (page == null || page <= 0) {
			page = 1;
		}
		Direction direction = Direction.DESC;
		PageRequest pageRequest = PageRequest.of(page - 1, 12, direction, order);
		QueryLectureByKeywordDTO2 p1 = null;
		if (bigcategory.isBlank() && midcategory.isBlank() && keyword.isBlank()) {
			p1 = lectureService.findAll(pageRequest);
		} else {
			p1 = lectureService.searchLectures(bigcategory, midcategory, keyword,
					pageRequest);
		}
		if(httpServletRequest.getHeader("Access_token") != null) {
			String userAccessToken = httpServletRequest.getHeader("Access_token");
			String userId = JWTUtil.getUserIdFromToken(userAccessToken);
			MemberEntity userEntity = memberService.findById(userId);
			p1 = lectureService.checkAlreadyHasLecture(userEntity , p1);
			return p1;
		} else {
			return p1;
		}
	}
	
	@GetMapping("/MentoHas/lecture")
	@ApiOperation(value = "멘토가 만든 강의 조회(멘토페이지용)", notes = "멘토페이지에서 로그인한 계정이 만든 강의의 이름과 가격을 반환") 
	public LectureOfMentoDtos getLectureOfMento(HttpServletRequest httpServletRequest, @RequestParam(value = "page", defaultValue = "0", required = false) Integer page) {
		String userAccessToken = httpServletRequest.getHeader("Access_token");
		String userId = JWTUtil.getUserIdFromToken(userAccessToken);
		MemberEntity userEntity = memberService.findById(userId);

		LectureOfMentoDtos pageLecture = lectureService.findByMentoId(userEntity , page);
		return pageLecture;
	}
}
