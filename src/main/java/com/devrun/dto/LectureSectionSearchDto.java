package com.devrun.dto;

import java.util.List;

import com.devrun.youtube.Video;

import lombok.Data;

@Data
public class LectureSectionSearchDto {
    private int SectionNumber; //1번
    private String SectionTitle; //1번 제목
    List<Video> videoDtos; //4
   
}