package com.devrun.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MyLecture;
import com.devrun.entity.MyLectureProgress;
import com.devrun.entity.MylectureNote;
import com.devrun.youtube.Video;

public interface MylectureNoteRepository extends JpaRepository<MylectureNote, Long> {
		
	Optional<Page<MylectureNote>> findByMyLectureProgress(MyLecture myLecture, PageRequest pageRequest);

	Optional<List<MylectureNote>> findByMyLectureProgressIn(List<MyLecture> myLectureList);

	List<MylectureNote> findByMyLectureProgressInOrderByDateDesc(List<MyLecture> list);

	MylectureNote findByNoteNo(Long noteNo);

	List<MylectureNote> findByMyLectureProgressInAndNoteDeleteFalseOrderByDateDesc(List<MyLectureProgress> myprogressList);

	Optional<Page<MylectureNote>> findByMyLectureProgressInAndNoteDeleteFalse(List<MyLectureProgress> myprogressList,
			PageRequest pageRequest);

}
