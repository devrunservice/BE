package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.LectureIntroduce;
import com.devrun.youtube.Lecture;


public interface LectureIntroduceRepository extends JpaRepository<LectureIntroduce, Long> {

	LectureIntroduce findByLecture(Lecture lecture);
	
	
	
}
