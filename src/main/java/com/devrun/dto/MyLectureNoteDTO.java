package com.devrun.dto;

import java.util.List;

import lombok.Data;

@Data
public class MyLectureNoteDTO {	
	private String lectureTitle,chapter,subHeading;
	private int count;
	private List<lectureNoteDetailDTO> lectureNoteDetailDTOList;

}
