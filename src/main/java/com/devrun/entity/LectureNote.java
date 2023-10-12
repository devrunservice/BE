package com.devrun.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class LectureNote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "noteNo")
	private Integer noteNo;

	@ManyToOne
	@JoinColumn(name = "mylectureNo", foreignKey = @ForeignKey(name = "FK_MyLecture_TO_LectureNote_1"))
	private MyLecture myLecture;

	@Column(name = "timepoint", nullable = false)
	private String timepoint;

	@Column(name = "Notecontext", columnDefinition = "TEXT", nullable = false)
	private String noteContext;

}
