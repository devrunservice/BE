package com.devrun.youtube;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturecategoryRepository extends JpaRepository<LectureCategory, Long> {
	@Override
	<S extends LectureCategory> S save(S entity);

	LectureCategory findByCategoryNoAndLectureBigCategoryAndLectureMidCategory(Long categoryNo,
			String lectureBigCategory, String lectureMidCategory);

	LectureCategory findByLectureBigCategoryAndLectureMidCategory(String bigcategory, String midcategory);

	List<LectureCategory> findByLectureBigCategoryOrLectureMidCategory(String bigcategory, String midcategory);
}
