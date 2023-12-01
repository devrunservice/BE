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
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Entity
@Data
@Table(name = "my_lecture_progress")
@EntityListeners(AuditingEntityListener.class)
public class MyLectureProgress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "my_lecture_progress_no", length = 5)
	private Long myLectureProgressNo;

	@ManyToOne
	@JoinColumn(name = "mylectureno")
	@JsonBackReference
	private MyLecture myLecture;
	
	@ManyToOne
	@JoinColumn(name = "video_id")
	@Comment("영상")
	@JsonBackReference
	private Video video;
	
	@Column(name = "progress")
	@Comment("영상 진행률")
	private int progress = 0;
	
	@Column(name = "timecheck" , nullable = true)
	@Comment("해당 영상 누적 시청 시간")
	private int timecheck = 0;
	
	@Column(name = "lastviewdate" , nullable = true)
	@Comment("마지막 영상 시청 시간")
    @LastModifiedDate
	private Date lastviewdate;	
}
