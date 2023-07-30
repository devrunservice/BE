package com.devrun.youtube;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;

    @Autowired
    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    public void saveLecture(CreateLectureRequestDto requestDto) {
        Lecture lecture = convertToLectureEntity(requestDto);
        lectureRepository.save(lecture);
    }

    private Lecture convertToLectureEntity(CreateLectureRequestDto requestDto) {
        Lecture lecture = new Lecture();
        lecture.setLectureIntro(requestDto.getLecutreIntro());
        lecture.setLectureName(requestDto.getLectureName());
        lecture.setLecturePrice(requestDto.getLecturePrice());
        lecture.setLectureThumbnail(requestDto.getLectureThumbnail());
        lecture.setLectureTag(requestDto.getLectureTag());

        List<LectureSection> sections = new ArrayList<>();
        for (LectureSectionDto sectionDto : requestDto.getLectureSection()) {
            LectureSection section = new LectureSection();
            section.setSectionNumber(sectionDto.getSectionNumber());
            section.setSectionTitle(sectionDto.getSectionTitle());

          
            }
        

        return lecture;
    }
}
