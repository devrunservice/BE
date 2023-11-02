package com.devrun.dto;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
public class lectureNoteDetailDTO {
	private Long noteId;
	private String noteTitle , content , subHeading;
	private int chapter;
	@Temporal(TemporalType.DATE)
	private Date date;

}
