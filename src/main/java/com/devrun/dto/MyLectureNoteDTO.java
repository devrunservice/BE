package com.devrun.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "강의 이름과 해당 강의에서 작성한 노트 갯수")
public class MyLectureNoteDTO {
	@ApiModelProperty(value = "강의 제목")
	private String lectureTitle;
	@ApiModelProperty(value = "강의 썸네일")
	private String lectureThumbnail;
	@ApiModelProperty(value = "강의 식별 번호")
	private Long lectureId;
	@ApiModelProperty(value = "강의에서 작성한 노트 갯수")
	private int count = 0;
	@ApiModelProperty(value = "최근 수강일")
	@Temporal(TemporalType.DATE)
	private Date lastStudyDate;
}
