package com.devrun.youtube;

import java.util.ArrayList;
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
    private String lectureDiscount;
    private String lectureDiscountrate;
    private String lectureDiscountstart;
    private String lectureDiscountend;
    private String lectureStatus;
    private String lectureThumbnail;
    private List<String> lectureTag;
    
    private List<LectureSectionDto> lectureSectionList; // 섹션 정보 리스트
    private LecturecategoryDto lectureCategory; // 카테고리 정보
    private List<VideoDto> videoList; // 비디오 정보 리스트
    
    
    public String getLectureBigCategory() {
        return lectureCategory.getLectureBigCategory();
    }

    public String getLectureMidCategory() {
        return lectureCategory.getLectureMidCategory();
    }
    public LectureSectionDto[] getLectureSection() {
        return lectureSectionList != null ? lectureSectionList.toArray(new LectureSectionDto[0]) : new LectureSectionDto[0];
    }

    public VideoDto[] getVideos() {
        return videoList != null ? videoList.toArray(new VideoDto[0]) : new VideoDto[0];
    }
	public MultipartFile getVideoFile() {
		return null;
	}
	
}
