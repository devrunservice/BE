package com.devrun.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureCategory;
import com.devrun.dto.LectureSectionSearchDto;
import com.devrun.dto.LecturecategorySearchDto;
import com.devrun.youtube.Video;
import com.devrun.repository.LectureSearchRepository;
import com.devrun.repository.VideoSearchRepository;
import com.devrun.youtube.LectureSection;
import com.devrun.youtube.LectureSectionRepository;
import com.devrun.youtube.LecturecategoryRepository;

@Service
public class LecuturesearchService {

    private final LectureSearchRepository lectureSearchRepository;
    private final LectureSectionRepository sectionRepository;
    private final LecturecategoryRepository categoryRepository;
    private final VideoSearchRepository videoSearchRepository;

    @Autowired
    public LecuturesearchService(LectureSearchRepository lectureSearchRepository, LectureSectionRepository sectionRepository,
        LecturecategoryRepository categoryRepository, VideoSearchRepository videoSearchRepository) {
        this.lectureSearchRepository = lectureSearchRepository;
        this.sectionRepository = sectionRepository;
        this.categoryRepository = categoryRepository;
        this.videoSearchRepository = videoSearchRepository;
    }

    public Lecture getLectureDetails(Long lectureId) throws NotFoundException {
        Lecture lecture = lectureSearchRepository.findById(lectureId)
            .orElseThrow(() -> new NotFoundException());

        return lecture;
    }

    public Lecture findByLectureID(Long lectureId) {
        Optional<Lecture> lecture = lectureSearchRepository.findByLectureid(lectureId);

        if (lecture.isPresent()) {
            return lecture.get();
        } else {
            throw new NoSuchElementException("Lecture Not Found!");
        }
    }

    public Lecture getLectureDetailsMapping(Long lectureId) throws NotFoundException {
        Lecture lecture = lectureSearchRepository.findById(lectureId)
            .orElseThrow(() -> new NotFoundException());

        // 강의 카테고리 정보 매핑
        LectureCategory lectureCategory = lecture.getLectureCategory();
        LecturecategorySearchDto categoryDto = new LecturecategorySearchDto();
        categoryDto.setCategoryNo(lectureCategory.getCategoryNo());
        categoryDto.setLectureBigCategory(lectureCategory.getLectureBigCategory());
        categoryDto.setLectureMidCategory(lectureCategory.getLectureMidCategory());

        // 강의 섹션 정보 매핑
        List<LectureSection> lectureSections = sectionRepository.findByLecture(lecture);
        List<LectureSectionSearchDto> sectionDtos = new ArrayList<>();
        for (LectureSection lectureSection : lectureSections) {
            LectureSectionSearchDto sectionDto = new LectureSectionSearchDto();
            sectionDto.setSectionNumber(lectureSection.getSectionNumber());
            sectionDto.setSectionTitle(lectureSection.getSectionTitle());

            // 섹션에 속한 비디오 정보 매핑
            List<Video> videos = videoSearchRepository.findByLectureSection(lectureSection);
            List<Video> videoDtos = new ArrayList<>();
            for (Video video : videos) {
                Video videoDto = new Video();
                videoDto.setFileName(video.getFileName());
                videoDto.setVideoId(video.getVideoId());
                videoDto.setVideoLink(video.getVideoLink());
                videoDto.setVideoTitle(video.getVideoTitle());
                videoDtos.add(videoDto);
            }
            sectionDto.setVideoDtos(videoDtos);
            sectionDtos.add(sectionDto);
        }

        Lecture detailsDto = new Lecture();
        detailsDto.setLectureName(lecture.getLectureName());
        detailsDto.setLectureIntro(lecture.getLectureIntro());
        detailsDto.setLecturePrice(lecture.getLecturePrice());
        detailsDto.setLectureStart(lecture.getLectureStart());
        detailsDto.setLectureEdit(lecture.getLectureEdit());
        detailsDto.setLectureThumbnail(lecture.getLectureThumbnail());
        detailsDto.setLectureCategory(lectureCategory);
//        detailsDto.setLectureSectionList(lectureSections);

        return detailsDto;
    }
}
