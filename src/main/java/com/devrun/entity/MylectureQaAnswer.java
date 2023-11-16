package com.devrun.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class MylectureQaAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "answer_no")
	private int answerNo;

	@OneToOne
	@JoinColumn(name = "qa_no")
	private MylectureQa qaNo;

	@ManyToOne
	@JoinColumn(name = "mento_no")
	private MemberEntity mentoNo;

	@Column(name = "answer")
	private String answer;

	@Column(name = "answer_date")
	@CreatedDate
	@LastModifiedDate
	@Temporal(TemporalType.DATE)
	private Date answerDate;
}
