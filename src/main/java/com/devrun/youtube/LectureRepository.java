package com.devrun.youtube;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devrun.entity.MemberEntity;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

	Lecture findByLectureName(String lecturename);


	Page<Lecture> findByLectureNameContainsOrLectureIntroContains(String keyword, String keyword2,
			PageRequest pageable);


	Page<Lecture> findByLectureNameContainsOrLectureIntroContainsOrMentoIdIn(String keyword, String keyword2,
			List<MemberEntity> m1, Pageable pageable);

}