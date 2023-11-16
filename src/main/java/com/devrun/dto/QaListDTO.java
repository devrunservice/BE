package com.devrun.dto;

import java.util.Date;

import lombok.Data;

@Data
public class QaListDTO {
	private String questionLectureTitle;
	private String questionTitle;
	private Date questionDate;
	private String questionContentPreview;
	private Long questionId;
	private int answer;
}
