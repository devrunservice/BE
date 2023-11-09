package com.devrun.youtube;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class VideoDto {
	private String videoId;
	private int totalPlayTime;
	private String videoLink;
	@NotBlank(message = "영상의 제목을 입력하세요")
	private String videoTitle;
	@NotNull(message = "영상을 첨부하세요")
	private MultipartFile videofile;
	@NotNull(message = "섹션 번호를 작성하세요")
	private int SectionNumber;
	@NotBlank(message = "섹션 제목를 작성하세요")
	private String SectionTitle;
}
