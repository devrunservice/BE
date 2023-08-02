package com.devrun.youtube;

public class VideoInfo {
    private String videoId;
    private String title;
    private String url;

    public VideoInfo(String videoId, String title, String url) {
        this.videoId = videoId;
        this.title = title;
        this.url = url;
    }

    public String getvideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}

