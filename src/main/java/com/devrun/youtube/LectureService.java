package com.devrun.youtube;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final LectureSectionRepository sectionRepository;
    private final LecturecategoryRepository categoryRepository; 
    private final VideoRepository videoRepository;

    @Autowired
    public LectureService(LectureRepository lectureRepository, LectureSectionRepository sectionRepository, LecturecategoryRepository categoryRepository, VideoRepository videoRepository) {
        this.lectureRepository = lectureRepository;
        this.sectionRepository = sectionRepository;
        this.categoryRepository = categoryRepository; 
        this.videoRepository = videoRepository;
    }

    // 강의 카테고리를 변환하는 메서드
    private LectureCategory convertToLectureCategoryEntity(LecturecategoryDto categoryDto) {
        LectureCategory lectureCategory = new LectureCategory();
        lectureCategory.setCategoryNo(categoryDto.getCategoryNo());
        lectureCategory.setLectureBigCategory(categoryDto.getLectureBigCategory());
        lectureCategory.setLectureMidCategory(categoryDto.getLectureMidCategory());
        return lectureCategory;
    }
    
    public void saveLecture(CreateLectureRequestDto requestDto) {
        // 강의 카테고리 저장
        LectureCategory lectureCategory = convertToLectureCategoryEntity(requestDto.getLectureCategory());
        lectureCategory = categoryRepository.save(lectureCategory); // LectureCategory 저장

        // 강의 저장
        Lecture lecture = convertToLectureEntity(requestDto, lectureCategory);
        lecture = lectureRepository.save(lecture); // Lecture 저장

        // LectureSection 저장
        for (LectureSectionDto sectionDto : requestDto.getLectureSectionList()) {
            LectureSection section = new LectureSection();
            section.setSectionNumber(sectionDto.getSectionNumber());
            section.setSectionTitle(sectionDto.getSectionTitle());
            section.setLecture(lecture); // Lecture와 연결
            sectionRepository.save(section); // LectureSection 저장
        }

        // Video 저장
        for (VideoDto videoDto : requestDto.getVideoList()) {
            Video video = new Video();
            video.setUploadDate(videoDto.getUploadDate());
            video.setFileName(videoDto.getFileName());
            video.setVideoId(videoDto.getVideoId());
            video.setTotalPlayTime(videoDto.getTotalPlayTime());
            video.setVideoLink(videoDto.getVideoLink());
            video.setVideoTitle(videoDto.getVideoTitle());

            Long lectureSectionId = videoDto.getLectureSectionId();
            if (lectureSectionId != null) {
                LectureSection section = sectionRepository.findById(lectureSectionId).orElse(null);
                video.setLectureSection(section);
            }

            video.setLecture(lecture); // Lecture와 연결
            videoRepository.save(video); // Video 저장
        }
    }

    
    private Lecture convertToLectureEntity(CreateLectureRequestDto requestDto, LectureCategory lectureCategory) {
        Lecture lecture = new Lecture();

        // CreateLectureRequestDto에서 필요한 데이터를 가져와서 Lecture 엔티티에 설정합니다.
        lecture.setLectureName(requestDto.getLectureName());
        lecture.setLectureIntro(requestDto.getLectureIntro());
        lecture.setLecturePrice(requestDto.getLecturePrice());
        lecture.setLectureThumbnail(requestDto.getLectureThumbnail());
        lecture.setLectureTag(requestDto.getLectureTag());

        // LectureCategory 객체를 Lecture 엔티티의 속성으로 설정합니다.
        lecture.setLectureCategory(lectureCategory);

     // LectureSection 설정
     // CreateLectureRequestDto에 있는 섹션 정보 리스트를 가져와서 LectureSection 엔티티 객체들을 생성하고 Lecture 엔티티와 연결합니다.
     List<LectureSection> sections = new ArrayList<>();
     for (LectureSectionDto sectionDto : requestDto.getLectureSectionList()) {
         LectureSection section = new LectureSection();
         section.setSectionNumber(sectionDto.getSectionNumber());
         section.setSectionTitle(sectionDto.getSectionTitle());

         // Lecture 객체와 연결 (LectureSection 객체에 Lecture 객체 설정)
         section.setLecture(lecture);

         sections.add(section);
     }
     lecture.setLectureSection(sections);


        // 비디오 설정
        List<Video> videos = new ArrayList<>();
        for (VideoDto videoDto : requestDto.getVideoList()) {
            Video video = new Video();
            video.setUploadDate(videoDto.getUploadDate());
            video.setFileName(videoDto.getFileName());
            video.setVideoId(videoDto.getVideoId());
            video.setTotalPlayTime(videoDto.getTotalPlayTime());
            video.setVideoLink(videoDto.getVideoLink());
            video.setVideoTitle(videoDto.getVideoTitle());

            // lectureSectionId를 VideoDto에서 가져와서 Video 엔티티에 설정합니다.
            Long lectureSectionId = videoDto.getLectureSectionId();
            if (lectureSectionId != null) {
                LectureSection section = sectionRepository.findById(lectureSectionId).orElse(null);
                video.setLectureSection(section);
            }
            videos.add(video);
        }
        lecture.setVideos(videos);

        // Lecture 엔티티를 데이터베이스에 저장합니다.
        lecture = lectureRepository.save(lecture);

        // 저장된 Lecture 엔티티를 반환합니다.
        return lecture;
    }
}

