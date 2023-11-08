package com.devrun.youtube;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class VideoDto {
	private Date uploadDate;
	private String fileName;
	private String videoId;
	private int totalPlayTime;
	private String videoLink;
	private String videoTitle;
	private MultipartFile videofile;
	private int SectionNumber;
	private String SectionTitle;
}
