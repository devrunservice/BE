package com.devrun.dto;

import java.util.Date;

import com.devrun.entity.MyLectureProgress;

import lombok.Data;

@Data
public class VideoInfo {
	
	/*
	 * 비디오 정보
	 * 개별 비디오 ID, 개별 비디오 전체 재생 시간, 개별 비디오 제목, 개별 비디오의 진행률, 마지막 영상 시청 시간
	 */
	private final String videoId;
	private final int videoTotalPlayTime;
	private final String videoTitle;
	private final int progress;
	private final int timecheck;
	private final Date lastviewdate;
	
	public VideoInfo(MyLectureProgress mlp) {
		super();
		this.videoId = mlp.getVideo().getVideoId();
		this.videoTotalPlayTime = mlp.getVideo().getTotalPlayTime();
		this.videoTitle = mlp.getVideo().getVideoTitle();
		this.progress = mlp.getProgress();
		this.timecheck = mlp.getTimecheck();
		this.lastviewdate = mlp.getLastviewdate();
	}
	
	
}
