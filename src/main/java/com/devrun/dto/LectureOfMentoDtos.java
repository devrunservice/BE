package com.devrun.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "강의 관리 데이터")
public class LectureOfMentoDtos {
	@ApiModelProperty(value = "전체 페이지")
	private int totalPages;
	@ApiModelProperty(value = "강의명, 강의가격")
	private List<LectureOfMentoDto> list;
}
