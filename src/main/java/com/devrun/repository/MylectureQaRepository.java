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

	Page<MylectureQa> findByUserNoAndAnswerNotNull(MemberEntity userEntity, PageRequest pageRequest);

	Page<MylectureQa> findByUserNoAndAnswerIsNull(MemberEntity userEntity, PageRequest pageRequest);

	Page<MylectureQa> findByUserNoAndQuestionTitleContainingOrUserNoAndQuestionContentContaining(
			MemberEntity userEntity, String keyword, MemberEntity userEntity2, String keyword2, PageRequest pageRequest);

	Page<MylectureQa> findByUserNoAndAnswerNotNullAndQuestionTitleContainingOrUserNoAndAnswerNotNullAndQuestionContentContaining(
			MemberEntity userEntity, String keyword, MemberEntity userEntity2, String keyword2, PageRequest pageRequest);

	Page<MylectureQa> findByUserNoAndAnswerIsNullAndQuestionTitleContainingOrUserNoAndAnswerIsNullAndQuestionContentContaining(
			MemberEntity userEntity, String keyword, MemberEntity userEntity2, String keyword2, PageRequest pageRequest);

	MylectureQa findByLectureQaNo(Long questionId);

}
