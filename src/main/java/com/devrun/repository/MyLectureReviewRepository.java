package com.devrun.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MylectureReview;
import com.devrun.youtube.Lecture;

public interface MyLectureReviewRepository extends JpaRepository<MylectureReview, Long> {

	Page<MylectureReview> findAllByLecture(Lecture lecture, PageRequest pageRequest);

}
