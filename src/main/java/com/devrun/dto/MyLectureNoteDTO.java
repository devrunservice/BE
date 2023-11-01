package com.devrun.dto;

import java.util.List;

import lombok.Data;

@Data
public class MyLectureNoteDTO {	
	private String lectureTitle,subHeading;
	private int count,chapter;
	private List<lectureNoteDetailDTO> lectureNoteDetailDTOList;

}
