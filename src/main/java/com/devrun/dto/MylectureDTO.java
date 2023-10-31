package com.devrun.dto;

import java.util.Date;

import com.devrun.entity.MyLecture;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
	}
	private final String title, mentoName, thumbnail;
	private final Date expiryDate;
	private final Date lastViewDate;
	private final float rating;
	private final int progressRate;
}
