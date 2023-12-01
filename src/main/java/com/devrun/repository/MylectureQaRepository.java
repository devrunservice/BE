package com.devrun.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MylectureQa.removed;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.MylectureQa;
import com.devrun.youtube.Lecture;

public interface MylectureQaRepository extends JpaRepository<MylectureQa, Long> {

	Page<MylectureQa> findByDeleteopAndLectureId(removed disable,Lecture lecture, PageRequest pageRequest);

	MylectureQa findByDeleteopAndUserNoAndLectureId(removed disable,MemberEntity userEntity, Lecture lecture);

	MylectureQa findByDeleteopAndLectureQaNo(removed disable,Long questionId);

	Page<MylectureQa> findByDeleteopAndUserNoAndCountGreaterThan(removed disable, MemberEntity userEntity, int i,
			PageRequest pageRequest);

	Page<MylectureQa> findByDeleteopAndUserNoAndCountIs(removed disable, MemberEntity userEntity, int i,
			PageRequest pageRequest);

	Page<MylectureQa> findByDeleteopAndUserNo(removed disable, MemberEntity userEntity, PageRequest pageRequest);

	Page<MylectureQa> findByDeleteopAndUserNoAndCountGreaterThanAndQuestionTitleContainingOrUserNoAndCountGreaterThanAndQuestionContentContaining(
			removed disable, MemberEntity userEntity, int i, String keyword, MemberEntity userEntity2, int j,
			String keyword2, PageRequest pageRequest);

	Page<MylectureQa> findByDeleteopAndUserNoAndCountIsAndQuestionTitleContainingOrUserNoAndCountIsAndQuestionContentContaining(
			removed disable, MemberEntity userEntity, int i, String keyword, MemberEntity userEntity2, int j,
			String keyword2, PageRequest pageRequest);

	Page<MylectureQa> findByDeleteopAndUserNoAndQuestionTitleContainingOrUserNoAndQuestionContentContaining(
			removed disable, MemberEntity userEntity, String keyword, int i, MemberEntity userEntity2, String keyword2,
			PageRequest pageRequest);


}
