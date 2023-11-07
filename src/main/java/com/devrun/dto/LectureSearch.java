package com.devrun.dto;

import java.util.Date;
import java.util.List;

import com.devrun.youtube.Video;

import lombok.Data;

@Data
public class LectureSearch {
	  private String lectureName;
	    private String lectureIntro;
	    private int lecturePrice;
	    private Date lectureStart;
	    private Date lectureEdit;
	    private String lectureThumbnail;
	    private List<String> lectureTag;
	    private LecturecategorySearchDto lectureCategory;
	    private List<LectureSectionSearchDto> lectureSectionList;
	    private List<Video> videoList;
}
