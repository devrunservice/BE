package com.devrun.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "장바구니 페이지에 전달될 강의 정보 객체")
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
	@ApiModelProperty(value = "강의 썸네일 URL")
	private String lectureThumbnail;
	// 제목
	@JsonProperty
	@ApiModelProperty(value = "강의명")
	private String lectureName;
	// 한줄소개글
	@JsonProperty
	@ApiModelProperty(value = "강의 한줄 소개")
	private String lectureIntro;
	// 가격
	@JsonProperty
	@ApiModelProperty(value = "강의 가격")
	private int lecturePrice;
	// 강의ID
	@JsonProperty
	@ApiModelProperty(value = "강의 고유 아이디")
	private Long lectureId;
	// 장바구니ID
	@JsonProperty
	@ApiModelProperty(value = "장바구니 고유 아이디")
	private Long cartId;
	
}
