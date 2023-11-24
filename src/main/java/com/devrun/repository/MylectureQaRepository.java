package com.devrun.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MemberEntity;
import com.devrun.entity.MylectureQa;
import com.devrun.youtube.Lecture;

public interface MylectureQaRepository extends JpaRepository<MylectureQa, Long> {

	Page<MylectureQa> findByUserNo(MemberEntity userEntity, PageRequest pageRequest);

	Page<MylectureQa> findByLectureId(Lecture lecture, PageRequest pageRequest);

	MylectureQa findByUserNoAndLectureId(MemberEntity userEntity, Lecture lecture);

	Page<MylectureQa> findByUserNoAndCountIs(MemberEntity userEntity, int i ,PageRequest pageRequest);

	Page<MylectureQa> findByUserNoAndCountGreaterThan(MemberEntity userEntity, int i , PageRequest pageRequest);

	Page<MylectureQa> findByUserNoAndQuestionTitleContainingOrUserNoAndQuestionContentContaining(
			MemberEntity userEntity, String keyword, MemberEntity userEntity2, String keyword2, PageRequest pageRequest);

	Page<MylectureQa> findByUserNoAndCountGreaterThanAndQuestionTitleContainingOrUserNoAndCountGreaterThanAndQuestionContentContaining(
			MemberEntity userEntity, int i ,String keyword, MemberEntity userEntity2, int i2 ,String keyword2, PageRequest pageRequest);

	Page<MylectureQa> findByUserNoAndCountIsAndQuestionTitleContainingOrUserNoAndCountIsAndQuestionContentContaining(
			MemberEntity userEntity, int i ,String keyword, MemberEntity userEntity2, int i2 ,String keyword2, PageRequest pageRequest);

	MylectureQa findByLectureQaNo(Long questionId);

}
