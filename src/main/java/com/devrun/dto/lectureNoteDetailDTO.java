package com.devrun.dto;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
public class lectureNoteDetailDTO {
	private Long noteId;
	private String noteTitle , content;

	@Temporal(TemporalType.DATE)
	private Date date;

	@Temporal(TemporalType.DATE)
	private Date lastModifiedDate;
}
