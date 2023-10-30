package com.devrun.youtube;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Data
@Entity
public class LectureSection {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sectionid")
    private Long sectionid;

    @Column(nullable = true)
    private int SectionNumber;
    
    @Column(nullable = true)
    private String SectionTitle;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonBackReference
    @JoinColumn(name = "lectureid")
    private Lecture lecture;
    
	@OneToMany(mappedBy = "lectureSection", cascade =  CascadeType.REMOVE)
	@JsonManagedReference
	private List<Video> videos;
  
}