package com.devrun.dto;

import com.devrun.youtube.Lecture;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "강의 검색 결과" , description = "강의 정보")
public class QueryLectureByKeywordDTO {

	// 일부 속성(강의명 , 강의 소개글, 강사명, 강의 평점, 강의 가격, 썸네일 URI , 카테고리 분류 중-소 , 속성)
	@ApiModelProperty(value = "강의 식별 번호")
	private Long lectureId;
	@ApiModelProperty(value = "강의 제목")
	private String lectureName;
	@ApiModelProperty(value = "강의 소개")
	private String lectureIntro;
	@ApiModelProperty(value = "강의 썸네일")
	private String lectureThumbnail;
	@ApiModelProperty(value = "강의 대분류명")
	private String lectureBigCategory;
	@ApiModelProperty(value = "강의 중분류명")
	private String lectureMidCategory;
	@ApiModelProperty(value = "강사 아이디")
	private String mentoId;
	@ApiModelProperty(value = "강의 가격")
	private int lecturePrice;
	@ApiModelProperty(value = "구입자 수")
	private int buyCount;
	@ApiModelProperty(value = "평점")
	private float rating;

	public QueryLectureByKeywordDTO(Lecture lecture) {
		this.lectureId = lecture.getLectureid();
		this.lectureName = lecture.getLectureName();
		this.lectureIntro = lecture.getLectureIntro();
		this.lectureThumbnail = lecture.getLectureThumbnail();
		this.lecturePrice = lecture.getLecturePrice();

		this.lectureBigCategory = lecture.getLectureCategory().getLectureBigCategory();
		this.lectureMidCategory = lecture.getLectureCategory().getLectureMidCategory();

		this.mentoId = lecture.getMentoId().getId();
		this.buyCount = lecture.getBuyCount();
		this.rating = lecture.getLectureRating();
	}

	public QueryLectureByKeywordDTO() {
		// TODO Auto-generated constructor stub
	}

}
