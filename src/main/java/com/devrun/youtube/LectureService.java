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
        Lecturecategory lectureCategory = new Lecturecategory();
        lectureCategory.setLectureBigCategory(requestDto.getLectureBigCategory());
        lectureCategory.setLectureMidCategory(requestDto.getLectureMidCategory());
        lectureCategory = lectureRepository.save(lectureCategory);

        lecture.setLectureCategory(lectureCategory);

        // 강의 섹션 설정
        List<LectureSection> sections = new ArrayList<>();
        for (LectureSectionDto sectionDto : requestDto.getLectureSection()) {
            LectureSection section = new LectureSection();
            section.setSectionNumber(sectionDto.getSectionNumber());
            section.setSectionTitle(sectionDto.getSectionTitle());
            section.setLecture(lecture); // 섹션과 강의를 연결해줌
            sectionRepository.save(section); // LectureSection 저장
            sections.add(section);
        }
        lecture.setLectureSection(sections);

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
            video.setLectureSection(videoDto.getLectureSection()); // 비디오와 섹션을 연결해줌
            videos.add(video);
        }
        lecture.setVideos(videos);

        return lecture;
    }
}
