package com.devrun.dto;

import java.util.Date;
import java.util.List;

import com.devrun.entity.MyLecture;

import lombok.Data;

@Data
public class MycouresDTO {
	/*
	 * 강의 정보 강의 제목, 강의 ID, 강의 총 진행률
	 */
	private final String lectureName;
	private final Long lectureId;
	private final Date lectureExpiryDate;
	private final int lectureWholeProgess;
	private final float lectureRating;
	private List<SectionInfo> sectionInfo;
	private int wholeStudyTime;
	private int wholeRemainingTime;

	public MycouresDTO(MyLecture my) {
		super();
		this.lectureName = my.getLecture().getLectureName();
		this.lectureId = my.getLecture().getLectureid();
		this.lectureExpiryDate = my.getLectureExpiryDate();
		this.lectureWholeProgess = my.getLectureProgress();
		this.lectureRating = my.getLecture().getLectureRating();
	}

}
