package com.devrun.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MyLecture;
import com.devrun.entity.MylectureNote;
import com.devrun.youtube.Video;

public interface MylectureNoteRepository extends JpaRepository<MylectureNote, Long> {
		
	Optional<Page<MylectureNote>> findByMyLecture(MyLecture myLecture, PageRequest pageRequest);

	Optional<List<MylectureNote>> findByMyLectureIn(List<MyLecture> myLectureList);

	List<MylectureNote> findByMyLectureInOrderByDateDesc(List<MyLecture> list);

	MylectureNote findByNoteNo(Long noteNo);

	Optional<Page<MylectureNote>> findByMyLectureAndNoteDeleteFalse(MyLecture myLecture, PageRequest pageRequest);

	List<MylectureNote> findByMyLectureInAndNoteDeleteFalseOrderByDateDesc(List<MyLecture> list);

}
