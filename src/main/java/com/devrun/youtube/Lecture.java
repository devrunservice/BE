package com.devrun.youtube;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.servlet.http.HttpServletResponse;

import lombok.Data;

@Data
@Entity
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
    private int lectureStart;
    
    @Column(nullable = true)
    private int lectureEdit;
    
    @Column(nullable = true)
    private String lectureDiscount;
    
    @Column(nullable = true)
    private String lectureDiscountrate;
    
    @Column(nullable = true)
    private String lectureDiscountstart;
    
    @Column(nullable = true)
    private String lectureDiscountend;
    
    @Column(nullable = true)
    private String lectureStatus;
    
    @Column(nullable = true)
    private String lectureThumbnail;

    @ElementCollection
    private List<String> lectureTag;

    @ManyToOne(cascade = CascadeType.PERSIST) 
    @JoinColumn(name = "categoryNo")
    private LectureCategory lectureCategory;
    
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL)
    private List<Video> videos;

	public void setLectureSection(List<LectureSection> sections) {
	}

	public HttpServletResponse getId() {
		return null;
	}

	
}

