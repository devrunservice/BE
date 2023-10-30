package com.devrun.youtube;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureSectionRepository extends JpaRepository<LectureSection, Long> {

	
	  @Query(value = "SELECT MAX(sectionid) FROM lecture_section", nativeQuery = true)
	    Long findLastSectionId();

	List<LectureSection> findByLectureSection(Lecture lecture);



}
