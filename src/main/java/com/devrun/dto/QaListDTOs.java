package com.devrun.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "작성된 질문 리스트 메타 정보")
public class QaListDTOs {

	private List<QaListDTO> dtolist;
	private int totalPages;
	private Long questionCount;
	}
