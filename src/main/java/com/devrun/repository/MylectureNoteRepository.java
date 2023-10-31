package com.devrun.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MyLecture;
import com.devrun.entity.MylectureNote;

public interface MylectureNoteRepository extends JpaRepository<MylectureNote, Long> {

	Optional<List<MylectureNote>> findByMyLecture(MyLecture myLecture);

	Optional<List<MylectureNote>> findByMyLectureIn(List<MyLecture> myLectureList);

	Optional<List<MylectureNote>> findByMyLectureInOrderByCreateDateDesc(List<MyLecture> list);

}
