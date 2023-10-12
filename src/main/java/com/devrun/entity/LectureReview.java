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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Data
public class LectureReview {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lectureReviewNo")
	private Long lectureReview;

	@Column(name = "ReviewBody", columnDefinition = "TEXT", nullable = false)
	private String reviewBody;

	@Column(name = "ReviewDay", nullable = false)
	@CreationTimestamp
	private Date reviewDay;

	@Column(name = "ReviewEditday", nullable = false)
	private Date reviewEditday;

	@Column(name = "ReviewRating", nullable = false)
	private Integer reviewRating;

	@ManyToOne
	@JoinColumn(name = "mylectureNo", referencedColumnName = "mylectureNo", foreignKey = @ForeignKey(name = "FK_MyLecture_TO_LectureReview_1"))
	private MyLecture myLecture;
}
