package com.devrun.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Comment;

import com.devrun.youtube.Lecture;

import lombok.Data;

@Entity
@Data
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

//	@Column(name = "passorfail")
//	@Comment("수강 완료 여부")
//	private boolean passOrFail;
//	
//	@Column(name = "rating")
//	@Comment("수강 평점")
//	private int rating;

}
