package com.devrun.youtube;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.devrun.entity.MemberEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@BatchSize(size = 100)
public class Lecture {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lectureid")
	private Long lectureid;

	@Column(nullable = true)
	private String lectureName;

	@Column(nullable = true)
	private String lectureIntro;

	@Column(nullable = true)
	private int lecturePrice;

	@Column(nullable = true)
	@CreatedDate
	@Temporal(TemporalType.DATE)
	private Date lectureStart;

	@Column(nullable = true)
	@LastModifiedDate
	@Temporal(TemporalType.DATE)
	private Date lectureEdit;

	@Column(nullable = true)
	private String lectureDiscount;

	@Column(nullable = true)
	private String lectureDiscountrate;

	@Column(nullable = true)
	private String lectureDiscountstart;

	@Column(nullable = true)
	private String lectureDiscountend;

	@Column(nullable = true)
	private String lectureStatus = "ACTIVE";

	@Column(nullable = true)
	private String lectureThumbnail;

	@Column(nullable = true)
	private float lectureRating;

	@ElementCollection
	private List<String> lectureTag;
	

	@Column(nullable = true)
	private int buyCount = 0;	
	
	
	@JoinColumn(name = "categoryNo")
	@ManyToOne
	private LectureCategory lectureCategory;

	@ManyToOne
	@JoinColumn(name = "userNo")
	private MemberEntity mentoId;
	
	//연관 관계 스택오버플로우 발생------------------------------
	
	@OneToMany(mappedBy = "lecture", cascade = CascadeType.REMOVE)
	@JsonManagedReference	
	private List<LectureSection> lectureSections;	
	
	//---------------------------------------------------
	// paymentEntity <-> lecture  <-> lectureSections 
	// lecturesections을 주석처리하면 오류 X 
	// 고민중..
	
	
	
	public void setLectureSections(List<LectureSection> sections) {
	    this.lectureSections = sections;
	}

	public HttpServletResponse getId() {
		return null;
	}
}
