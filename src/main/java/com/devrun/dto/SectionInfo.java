package com.devrun.dto;

import java.util.List;

import com.devrun.youtube.LectureSection;

import lombok.Data;

@Data
public class SectionInfo {
	/*
	 * 섹션 정보 섹션 ID, 섹션 제목, 섹션 번호
	 */
	private final Long sectionId;
	private final int sectionNumber;
	private final String sectionTitle;

	private List<VideoInfo> videoInfo;

	public SectionInfo(LectureSection ls) {
		super();
		this.sectionId = ls.getSectionid();
		this.sectionNumber = ls.getSectionNumber();
		this.sectionTitle = ls.getSectionTitle();
	}

}
