package com.devrun.youtube;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.BatchSize;

import lombok.Data;

@Data
@Entity
@BatchSize(size = 100)
public class LectureCategory {
	 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryNo")
    @NotNull(message = "카테고리 No를 입력하세요")
    private Long categoryNo;

    @Column(nullable = true)
    @NotBlank(message = "카테고리 대분류를 입력하세요")
    private String lectureBigCategory;
    
    @Column(nullable = true)
    @NotBlank(message = "카테고리 중분류를 입력하세요")
    private String lectureMidCategory;


}