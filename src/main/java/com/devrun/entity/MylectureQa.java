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

import com.devrun.youtube.Lecture;
import com.devrun.youtube.Video;

import lombok.Data;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class MylectureQa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lecture_qa_no")
	private Long lectureQaNo;

	@ManyToOne
	@JoinColumn(name = "lecture_id")
	private Lecture lectureId;

	@ManyToOne
	@JoinColumn(name = "video_id")
	private Video videoId;

	@ManyToOne
	@JoinColumn(name = "user_no")
	private MemberEntity userNo;

	@Column(name = "question_content", columnDefinition = "TEXT", nullable = false)
	private String questionContent;

	@Column(name = "question_date", nullable = false)
	@CreatedDate
	@LastModifiedDate
	@Temporal(TemporalType.DATE)
	private Date questionDate;
	

	@Column(name = "comment_count")
	private int count=0;

	@Column(name = "question_title", nullable = false)
	private String questionTitle;

	@Column(name = "question_delete", nullable = false)
	private boolean questionDelete = false;
}
