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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class MylectureReview {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mylecture_review_no")
	private Long mylectureReviewNo;

	@ManyToOne
	@JoinColumn(name = "mylecture_no")
	private final MyLecture myLecture;
	
	@Column(name = "review_content", columnDefinition = "TEXT", nullable = false)
	private final String reviewContent;

	@Column(name = "review_day", nullable = false)
	@CreatedDate
    @Temporal(TemporalType.DATE)
	private Date reviewDate;

	@Column(name = "review_rating", nullable = false)
	private final float reviewRating;

}
