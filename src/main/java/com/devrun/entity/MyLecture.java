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
	
	@Column(name="lecture_expiry_date")
    @Temporal(TemporalType.DATE)
	private Date lectureExpiryDate;
	
	@Column(name="wholeprogress")
	private int lectureProgress;
	
	@Column(name = "lastviewdate" , nullable = false)
	@Comment("마지막으로 해당 강의를 학습한 날짜")
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
