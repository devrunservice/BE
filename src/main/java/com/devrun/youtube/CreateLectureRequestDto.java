package com.devrun.youtube;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateLectureRequestDto {
    private String lectureName;
    private String lectureIntro;
    private int lecturePrice;
    private int lectureStart;
    private int lectureEdit;
    private MultipartFile lectureThumbnailFile;
    private List<String> lectureTag;
    private LectureCategory lectureCategory; // 카테고리 정보
    private LectureSectionDto lectureSectionList; // 섹션 정보 리스트 List
    private List<VideoDto> videoList; // 비디오 정보 리스트 List
}
