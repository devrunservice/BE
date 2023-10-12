package com.devrun.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Data
public class LectureQa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lectureQaNo")
	private Integer lectureQaNo;

	@ManyToOne
	@JoinColumn(name = "mylectureNo", foreignKey = @ForeignKey(name = "FK_MyLecture_TO_LectureQa_1"))
	private MyLecture myLecture;

	@Column(name = "lequestionBody", columnDefinition = "TEXT", nullable = false)
	private String lequestionBody;

	@Column(name = "lequestionDate", nullable = false)
	@CreationTimestamp
	private Date lequestionDate;

	@Column(name = "lequestionTitle", nullable = false)
	private String lequestionTitle;

	@Column(name = "leViews", nullable = false)
	private Integer leViews = 0;

	@Column(name = "leLikes", nullable = false)
	private Integer leLikes = 0;

	@Column(name = "lequestionDelete", nullable = false)
	private boolean lequestionDelete = false;
}
