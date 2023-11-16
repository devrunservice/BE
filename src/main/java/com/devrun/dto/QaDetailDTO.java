package com.devrun.dto;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
public class QaDetailDTO {
	private Long questionId;
	private Long lectureId;
	private Long videoId;
	private String lectureTitle;
	private String studentId;
	private String questionTitle;
	private String content;
	private String answer;
	@Temporal(TemporalType.DATE)
	private Date date;
}
