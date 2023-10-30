package com.devrun.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MemberEntity;
import com.devrun.entity.MyLecture;
import com.devrun.entity.MylectureReview;

public interface MyLectureReviewRepository extends JpaRepository<MylectureReview, Long> {

	Optional<MylectureReview> findByMyLectureAndMylectureReviewNo(MyLecture userEntity, Long mylectureReviewNo);

	
}
