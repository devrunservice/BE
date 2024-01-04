package com.devrun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureSection;
import com.devrun.youtube.Video;

public interface VideoSearchRepository extends JpaRepository<Video, Long> {

	Video findByVideoNo(Long videoNo);

	Video findByVideoId(String videoId);

	// 현재 영상의 이전 강의 영상을 검색
	Video findPreviousVideoByVideoIdLessThan(Long videoId);

	// 현재 영상의 다음 강의 영상을 검색
	Video findNextVideoByVideoIdGreaterThan(Long videoId);

	List<Video> findByLectureSection(LectureSection lectureSection);

	List<Video> findByLecture(Lecture lecture);

}
