package com.devrun.dto;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "노트 상세 정보")
public class lectureNoteListDTO {
	@ApiModelProperty(value = "노트 식별 번호")
	private Long noteId;
	@ApiModelProperty(value = "영상 id")
	private String VideoId;
	@ApiModelProperty(value = "노트 제목")
	private String noteTitle;
	@ApiModelProperty(value = "노트 내용 미리보기")
	private String contentPreview; 
	@ApiModelProperty(value = "섹션 제목")
	private String subHeading;
	@ApiModelProperty(value = "섹션 번호")
	private int chapter;
	@ApiModelProperty(value = "작성일")
	@Temporal(TemporalType.DATE)
	private Date date;

}
