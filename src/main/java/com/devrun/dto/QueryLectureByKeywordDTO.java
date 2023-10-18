package com.devrun.dto;

import com.devrun.youtube.Lecture;

import lombok.Data;

@Data
public class QueryLectureByKeywordDTO {

	// 일부 속성(강의명 , 강의 소개글, 강사명, 강의 평점, 강의 가격, 썸네일 URI , 카테고리 분류 중-소 , 속성)

	private String lectureName, lectureIntro, lectureThumbnail, lectureBigCategory, lectureMidCategory, mentoId;
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
