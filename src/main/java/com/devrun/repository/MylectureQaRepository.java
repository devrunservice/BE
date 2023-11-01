package com.devrun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.entity.MyLecture;
import com.devrun.entity.MylectureQa;

public interface MylectureQaRepository extends JpaRepository<MylectureQa, Long> {

	List<MylectureQa> findByMyLectureIn(List<MyLecture> list);

}
