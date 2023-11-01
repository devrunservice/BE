package com.devrun.dto;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
public class QaDTO {
	private Long lectureQaNo;
	private String lectureTitle;
	private String mentoId;
	private String questionTitle;
	private String questionContent;
	@Temporal(TemporalType.DATE)
	private Date questionDate;
}
