package com.devrun.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "작성된 질문 리스트 간략한 정보")
public class QaListDTO {
	private String questionLectureTitle;
	private String questionTitle;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date questionDate;
	private String questionContentPreview;
	private Long questionId;
	private String studentId;
	@ApiModelProperty(value = "댓글 수 , 0 = 답변 대기 중")
	private int answer;
}
