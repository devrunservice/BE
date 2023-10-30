package com.devrun.entity;



import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class MylectureQa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lecture_qa_no")
	private Integer lectureQaNo;

	@ManyToOne
	@JoinColumn(name = "mylecture_no")
	private MyLecture myLecture;

	@Column(name = "question_content", columnDefinition = "TEXT", nullable = false)
	private String questionContent;

	@Column(name = "question_date", nullable = false)
	@CreatedDate
    @Temporal(TemporalType.DATE)
	private Date questionDate;

	@Column(name = "question_title", nullable = false)
	private String questionTitle;

	@Column(name = "question_delete", nullable = false)
	private boolean questionDelete = false;
}
