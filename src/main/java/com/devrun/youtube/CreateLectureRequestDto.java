package com.devrun.youtube;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateLectureRequestDto {
	@NotBlank(message = "강의 이름을 작성하세요")
	private String lectureName;
	@NotBlank(message = "강의 소개를 작성하세요")
	private String lectureIntro;
	@NotNull(message = "강의 가격을 작성하세요")
	@Min(value = 0)
	@Max(value = 9999999)
	private int lecturePrice;
	private Date lectureStart;
	private Date lectureEdit;
	private MultipartFile lectureThumbnail;
	private List<String> lectureTag;
	@Valid
	private LectureCategory lectureCategory; // 카테고리 정보
	@Valid
	private List<LectureSectionDto> lectureSectionList; // 섹션 정보 리스트 List
	@Valid
	private List<VideoDto> videoList; // 비디오 정보 리스트 List
}
