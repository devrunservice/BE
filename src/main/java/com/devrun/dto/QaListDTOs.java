package com.devrun.dto;

import java.util.List;

import lombok.Data;

@Data
public class QaListDTOs {

	private List<QaListDTO> dtolist;
	private int totalPages;
	private Long questionCount;
	}
