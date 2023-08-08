package com.devrun.youtube;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class LectureCategory {
	 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryNo") 
    private Long categoryNo;

    @Column(nullable = true)
    private String lectureBigCategory;
    
    @Column(nullable = true)
    private String lectureMidCategory;


}