package com.devrun.dto;

import javax.persistence.Column;

import lombok.Data;

@Data
public class LectureOfMentoDto {
	
	private String lectureName;
	private String lectureStatus;
	private int lecturePrice;
	private int no;

}
