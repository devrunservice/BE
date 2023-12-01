package com.devrun.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@ApiModel(value = "내 학습 강의 객체")
@RequiredArgsConstructor
public class MylectureDTO2 {
	
	@ApiModelProperty(value = "내 학습 강의 목록")
	private final List<MylectureDTO> dtolist;
	@ApiModelProperty(value = "전체 페이지 수")
	private final int totalPages;
}
