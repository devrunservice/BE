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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.devrun.youtube.Lecture;
import com.devrun.youtube.Video;

import lombok.Data;

@Entity
@Data
@Table(name = "mylecture")
@EntityListeners(AuditingEntityListener.class)
public class MyLecture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mylectureNo", length = 5)
	@Comment("유저 강의 목록")
	private Long mylectureno;

	@ManyToOne
	@JoinColumn(name = "userNo")
	@Comment("수강생")
	private MemberEntity memberentity;

	@ManyToOne
	@JoinColumn(name = "lectureNo")
	@Comment("강의")
	private Lecture lecture;
	
	@ManyToOne
	@JoinColumn(name = "videoNo")
	@Comment("영상")
	private Video video;
	
	@Column(name = "progress")
	@Comment("영상 진행률")
	private int progress = 0;
	
	@Column(name = "timecheck" , nullable = true)
	@Comment("영상 누적 시청 시간")
	private int timecheck = 1;
	
	@Column(name = "lastviewdate" , nullable = true)
	@Comment("마지막 영상 시청 시간")
    @LastModifiedDate
    @Temporal(TemporalType.DATE)
	private Date lastviewdate;
	
	
	

//	@Column(name = "passorfail")
//	@Comment("수강 완료 여부")
//	private boolean passOrFail;
//	
//	@Column(name = "rating")
//	@Comment("수강 평점")
//	private int rating;

}
