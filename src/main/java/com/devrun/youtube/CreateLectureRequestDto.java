package com.devrun.youtube;

import java.util.List;
import lombok.*;

@Data
public class CreateLectureRequestDto {
	  	private String LectureName;
	  	private String LecutreIntro;
	    private int LecturePrice;
	    private int LectureStart;
	    private int LectureEdit;
	  	private String LectureDiscount;
	  	private String LectureDiscountrate;
	  	private String LectureDiscountstart;
	  	private String LectureDiscountend;
	  	private String LectureStatus;
	    private String lectureThumbnail;
	    private List<String> lectureTag;
	    private List<LectureSectionDto> lectureSection;
}
