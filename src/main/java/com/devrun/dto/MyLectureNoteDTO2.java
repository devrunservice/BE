package com.devrun.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "lectureNoteOpen 리스트 객체")
public class MyLectureNoteDTO2 {	
	@ApiModelProperty(value = "강의 - 노트 데이터")
	private List<MyLectureNoteDTO> dtolist;
	@ApiModelProperty(value = "전체 페이지 수")
	private int totalPages;
}
