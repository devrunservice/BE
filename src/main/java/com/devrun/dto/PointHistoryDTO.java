package com.devrun.dto;

import org.springframework.data.domain.Page;

import com.devrun.repository.PointHis;

import lombok.Data;

@Data
public class PointHistoryDTO {
	
	 private int mypoint;
	 private Page<PointHis> pointHistoryPage;

	    public PointHistoryDTO(int mypoint, Page<PointHis> pointHistoryPage) {
	        this.mypoint = mypoint;
	        this.pointHistoryPage = pointHistoryPage;
	    }

}
