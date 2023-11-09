package com.devrun.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "리스트 객체")
public class lectureNoteListDTO2 {
	@ApiModelProperty(value = "특정 강의의 노트 데이터")
	private List<lectureNoteListDTO> dtolist;
	@ApiModelProperty(value = "전체 페이지 수")
	private int totalpages;

}
