package com.devrun.dto;

import java.util.Date;

import com.devrun.entity.MyLecture;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "수강 강의 정보")
public class MylectureDTO {
	public MylectureDTO(MyLecture my) {
		super();
		this.title = my.getLecture().getLectureName();
		this.mentoName = my.getLecture().getMentoId().getId();
		this.thumbnail = my.getLecture().getLectureThumbnail();
		this.expiryDate = my.getLectureExpiryDate();
		this.lastViewDate = my.getLastviewdate();
		this.rating = my.getLecture().getLectureRating();
		this.progressRate = my.getLectureProgress();
		this.Id = my.getLecture().getLectureid();
	}
	@ApiModelProperty(value = "강의 제목")
	private final String title;
	@ApiModelProperty(value = "강사 아이디")
	private final String mentoName;
	@ApiModelProperty(value = "강의 썸네일")
	private final String thumbnail;
	@ApiModelProperty(value = "강의 만료일")
	private final Date expiryDate;
	@ApiModelProperty(value = "최근 학습일")
	private final Date lastViewDate;
	@ApiModelProperty(value = "해당 강의의 종합 평점")
	private final float rating;
	@ApiModelProperty(value = "진도율")
	private final int progressRate;
	@ApiModelProperty(value = "강의 식별 번호")
	private final Long Id;
}
