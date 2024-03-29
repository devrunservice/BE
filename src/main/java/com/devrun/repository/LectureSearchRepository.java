package com.devrun.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devrun.youtube.Lecture;
import com.devrun.entity.MemberEntity;
import com.devrun.youtube.LectureCategory;

@Repository
public interface LectureSearchRepository extends JpaRepository<Lecture, Long>, JpaSpecificationExecutor<Lecture> {

	Lecture findByLectureName(String lecturename);

	@Query(value = "SELECT * FROM lecture WHERE lecture_name LIKE %:keyword% OR lecture_intro LIKE %:keyword%", nativeQuery = true)
	Page<Lecture> findByLectureNameContainsOrLectureIntroContains(@Param("keyword") String keyword,
			PageRequest pageable);

	@Query(value = "SELECT * FROM lecture WHERE lecture_name LIKE %:keyword% OR lecture_intro LIKE %:keyword% OR mento_id IN :mentoIds", nativeQuery = true)
	Page<Lecture> findByLectureNameContainsOrLectureIntroContainsOrMentoIdIn(@Param("keyword") String keyword,
			@Param("mentoIds") List<MemberEntity> m1, Pageable pageable);

	@Query(value = "SELECT * FROM lecture WHERE category_no IN :categoryNumbers "
			+ "AND (lecture_name LIKE %:keyword% OR lecture_intro LIKE %:keyword%)", nativeQuery = true)
	Page<Lecture> findLecturesWithCategroy(@Param("categoryNumbers") List<LectureCategory> categorys,
			@Param("keyword") String keyword, PageRequest pageRequest);

	@Query(value = "SELECT * FROM lecture WHERE category_no = :categoryNumber "
			+ "AND (lecture_name LIKE %:keyword% OR lecture_intro LIKE %:keyword%)", nativeQuery = true)
	Page<Lecture> findLecturesWithCategroy(@Param("categoryNumber") LectureCategory category,
			@Param("keyword") String keyword, PageRequest pageRequest);

	Lecture findByLectureNameAndMentoId(String lectureName, MemberEntity mentoEntity);

	Optional<Lecture> findByLectureid(Long lectureid);

}