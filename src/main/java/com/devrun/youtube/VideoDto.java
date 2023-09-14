package com.devrun.youtube;

import java.sql.Date;

import lombok.Data;

@Data
public class VideoDto {
    private Date uploadDate;
    private String fileName;
    private String videoId;
    private String totalPlayTime;
    private String videoLink;
    private String videoTitle;
    private Long lectureSectionId; // 섹션과의 연결을 위한 섹션 ID
    // lectureSectionId Setter 추가
    public void setLectureSectionId(Long lectureSectionId) {
        this.lectureSectionId = lectureSectionId;
    }

    
    
    public LectureSection getLectureSection(LectureSectionRepository sectionRepository) {
        if (lectureSectionId != null) {
            return sectionRepository.findById(lectureSectionId).orElse(null);
        }
        return null; 
    }
    
	public void setLectureSection(LectureSectionDto sectionDto) {
		
	}
}
