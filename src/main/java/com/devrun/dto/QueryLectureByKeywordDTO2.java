package com.devrun.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "강의 검색 결과와 페이지 정보" , description = "강의 정보와 페이지 수, 검색된 전체 강의 갯수를 출력")
public class QueryLectureByKeywordDTO2 {

	@ApiModelProperty(value = "강의 검색 결과")
	private List<QueryLectureByKeywordDTO> Dtolist;
	@ApiModelProperty(value = "검색 페이지 수")
	private int totalpages;
	@ApiModelProperty(value = "검색 결과 갯수")
	private Long totalelements;

}
