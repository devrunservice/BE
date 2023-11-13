package com.devrun.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;

import com.devrun.youtube.Lecture;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@Entity
@Data
public class LectureIntroduce {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonBackReference
    private Long introNo;
	
	@OneToOne
	@JoinColumn(name = "lectureid")
	@JsonBackReference
	private Lecture lecture;
	
	@NotBlank
	private String content;
}
