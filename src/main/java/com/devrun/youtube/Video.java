package com.devrun.youtube;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
	
	@Entity
	@Getter
	@Setter
	public class Video {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "videoNo")
	    private Long videoNo;
	    
	    @Column(nullable = true)
	    private Date uploadDate;
	    
	    @Column(nullable = true)
	    private String fileName;
	    
	    @Column(nullable = true)
	    private String videoId;
	
	    @Column(nullable = true)
	    private String totalPlayTime;
	
	    @Column(nullable = true)
	    private String videoLink;
	
	    @Column(nullable = true)
	    private String videoTitle;
	    
	    @ManyToOne(cascade = CascadeType.PERSIST)
	    @JoinColumn(name = "lecture_id")
	    private Lecture lecture;
	    
	    @ManyToOne(cascade = CascadeType.PERSIST)
	    @JoinColumn(name = "sectionid")
	    private LectureSection lectureSection;
	    
	    
	}