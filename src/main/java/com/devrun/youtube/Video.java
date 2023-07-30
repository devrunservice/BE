package com.devrun.youtube;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
	
	@Entity
	@Getter
	@Setter
	public class Video {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long videoNo;
	    
	    @Column(nullable = true)
	    private Date uploadDate;
	    
	    @Column(nullable = true)
	    private String fileName;
	    
	    @Column(nullable = true)
	    private String videoId;
	
	    @Column(nullable = true)
	    private Integer totalPlayTime;
	
	    @Column(nullable = true)
	    private String videoLink;
	
	    @Column(nullable = true)
	    private String videoTitle;
	    
	    
	 
	    
	    
	}