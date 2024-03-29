package com.devrun.dto;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "노트 세부 내용")
public class lectureNoteDetailDTO {
	@ApiModelProperty(value = "노트 식별 번호")
	private Long noteId;
	@ApiModelProperty(value = "노트를 작성했던 영상 ID")
	private String VideoId;
	@ApiModelProperty(value = "노트 제목")
	private String noteTitle;
	@ApiModelProperty(value = "노트 내용")
	private String content;
	@ApiModelProperty(value = "섹션 제목")
	private String subHeading;
	@ApiModelProperty(value = "섹션 번호")
	private int chapter;
	@ApiModelProperty(value = "작성일")
	@Temporal(TemporalType.DATE)
	private Date date;

}
