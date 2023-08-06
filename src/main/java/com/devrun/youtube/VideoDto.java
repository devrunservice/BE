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
    public LectureSection getLectureSection() {
        LectureSection section = new LectureSection();
        section.setSectionid(lectureSectionId); // 섹션 ID를 설정하여 실제 객체를 찾도록 함
        return section;
    }
}
