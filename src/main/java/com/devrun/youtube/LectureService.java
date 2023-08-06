package com.devrun.youtube;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final LectureSectionRepository sectionRepository;

    @Autowired
    public LectureService(LectureRepository lectureRepository, LectureSectionRepository sectionRepository) {
        this.lectureRepository = lectureRepository;
        this.sectionRepository = sectionRepository;
    }

    public void saveLecture(CreateLectureRequestDto requestDto) {
        Lecture lecture = convertToLectureEntity(requestDto);
        lectureRepository.save(lecture);
    }

    private Lecture convertToLectureEntity(CreateLectureRequestDto requestDto) {
        Lecture lecture = new Lecture();
        lecture.setLectureName(requestDto.getLectureName());
        lecture.setLectureIntro(requestDto.getLectureIntro());
        lecture.setLecturePrice(requestDto.getLecturePrice());
        lecture.setLectureThumbnail(requestDto.getLectureThumbnail());
        lecture.setLectureTag(requestDto.getLectureTag());

        // 강의 카테고리 설정
        LectureCategory lectureCategory = new LectureCategory();
        lectureCategory.setLectureBigCategory(requestDto.getLectureBigCategory());
        lectureCategory.setLectureMidCategory(requestDto.getLectureMidCategory());

        lecture.setLectureCategory(lectureCategory);

        // 강의 섹션 설정
        List<LectureSection> sections = new ArrayList<>();
        for (LectureSectionDto sectionDto : requestDto.getLectureSection()) {
            LectureSection section = new LectureSection();
            section.setSectionNumber(sectionDto.getSectionNumber());
            section.setSectionTitle(sectionDto.getSectionTitle());
            section.setLecture(lecture); // Lecture 객체와 연결
            sectionRepository.save(section); // LectureSection 저장
            sections.add(section);
        }
        lecture.setLectureSection(sections);
        
        // 강의를 먼저 저장해야 LectureSection에서 Lecture 객체가 영속성 컨텍스트에 존재합니다.
        lecture = lectureRepository.save(lecture);

        // 강의 섹션을 Lecture와 연결한 후에 LectureSection 저장
        for (LectureSection section : sections) {
            sectionRepository.save(section);
        }
        // 비디오 설정
        List<Video> videos = new ArrayList<>();
        for (VideoDto videoDto : requestDto.getVideos()) {
            Video video = new Video();
            video.setUploadDate(videoDto.getUploadDate());
            video.setFileName(videoDto.getFileName());
            video.setVideoId(videoDto.getVideoId());
            video.setTotalPlayTime(videoDto.getTotalPlayTime());
            video.setVideoLink(videoDto.getVideoLink());
            video.setVideoTitle(videoDto.getVideoTitle());
         // 비디오에 해당하는 섹션을 찾아서 연결해줌
            LectureSection section = sectionRepository.findById(videoDto.getLectureSection().getSectionid()).orElse(null);
            video.setLectureSection(section);
            videos.add(video);
        }
        lecture.setVideos(videos);
        lecture = lectureRepository.save(lecture);

        return lecture;
    }
}
