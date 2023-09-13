package com.devrun.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LectureInfo {
	
	 public LectureInfo(String lecture_thumbnail, String lecture_name, String lecture_intro, int lecture_price) {
		super();
		this.lecture_thumbnail = lecture_thumbnail;
		this.lecture_name = lecture_name;
		this.lecture_intro = lecture_intro;
		this.lecture_price = lecture_price;
	}
	//썸네일
	 @JsonProperty
	 private String lecture_thumbnail;
	 //제목
	 @JsonProperty
	 private String lecture_name;
	 //한줄소개글
	 @JsonProperty
	 private String lecture_intro;
	 //가격
	 @JsonProperty
	 private int lecture_price;
}