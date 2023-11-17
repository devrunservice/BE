package com.devrun.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "질문 세부 내용")
public class QaDetailDTO {
	private Long questionId;
	private Long lectureId;
	private String videoId;
	private String lectureTitle;
	private String studentId;
	private String mentoId;
	private String mentoProfileImg;
	private String questionTitle;
	private String content;
	private String answer;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date date;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date answerDate;
}
