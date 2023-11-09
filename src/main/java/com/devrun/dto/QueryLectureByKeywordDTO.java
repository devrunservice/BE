package com.devrun.dto;

import com.devrun.youtube.Lecture;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "강의 검색 결과" , description = "강의 정보")
public class QueryLectureByKeywordDTO {

	// 일부 속성(강의명 , 강의 소개글, 강사명, 강의 평점, 강의 가격, 썸네일 URI , 카테고리 분류 중-소 , 속성)
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
	private int lectureprice;

	public QueryLectureByKeywordDTO(Lecture leture) {
		this.lectureName = leture.getLectureName();
		this.lectureIntro = leture.getLectureIntro();
		this.lectureThumbnail = leture.getLectureThumbnail();
		this.lectureprice = leture.getLecturePrice();

		this.lectureBigCategory = leture.getLectureCategory().getLectureBigCategory();
		this.lectureMidCategory = leture.getLectureCategory().getLectureMidCategory();

		this.mentoId = leture.getMentoId().getId();
	}

	public QueryLectureByKeywordDTO() {
		// TODO Auto-generated constructor stub
	}

}
