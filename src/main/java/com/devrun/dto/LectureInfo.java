package com.devrun.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LectureInfo {

	public LectureInfo(String lecture_thumbnail, String lecture_name, String lecture_intro, int lecture_price,
			Long lecture_id , Long cartId) {
		this.lectureThumbnail = lecture_thumbnail;
		this.lectureName = lecture_name;
		this.lectureIntro = lecture_intro;
		this.lecturePrice = lecture_price;
		this.lectureId = lecture_id;
		this.cartId = cartId;
	}

	// 썸네일
	@JsonProperty
	private String lectureThumbnail;
	// 제목
	@JsonProperty
	private String lectureName;
	// 한줄소개글
	@JsonProperty
	private String lectureIntro;
	// 가격
	@JsonProperty
	private int lecturePrice;
	// 강의ID
	@JsonProperty
	private Long lectureId;
	// 장바구니ID
	@JsonProperty
	private Long cartId;
	
}
