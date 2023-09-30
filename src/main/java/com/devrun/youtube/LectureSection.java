package com.devrun.youtube;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.jpa.repository.Query;

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
    @JoinColumn(name = "lectureid")
    private Lecture lecture;
  
}