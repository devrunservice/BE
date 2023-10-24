package com.devrun.youtube;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateLectureRequestDto {
    private String lectureName;
    private String lectureIntro;
    private int lecturePrice;
    private Date lectureStart;
    private Date lectureEdit;
    private MultipartFile lectureThumbnailFile;
    private List<String> lectureTag;
    private LectureCategory lectureCategory; // 카테고리 정보
    private List<LectureSectionDto> lectureSectionList; // 섹션 정보 리스트 List
    private List<VideoDto> videoList; // 비디오 정보 리스트 List
}
