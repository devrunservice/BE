package com.devrun.youtube;

import java.util.Date;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoInfo {
    private String videoId;
    private String url; // URL 정보 추가

    public String getVideoId() {
        return videoId;
    }

    public String getUrl() {
        return url;
    }

	public Date getUploadDate() {
		return null;
	}

	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTotalPlayTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVideoLink() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVideoTitle() {
		// TODO Auto-generated method stub
		return null;
	}
}
