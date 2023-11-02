package com.devrun.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
public class MyLectureNoteDTO {	
	private String lectureTitle , lectureThumnail;
	private Long lectureId;
	private int count = 0;
	@Temporal(TemporalType.DATE)
	private Date lastStudyDate;
}
