package com.devrun.youtube;

public class VideoInfo {
    private String videoId;
    private String url;

    public VideoInfo(String videoId, String url) {
        this.videoId = videoId;
   
        this.url = url;
    }

    public String getvideoId() {
        return videoId;
    }

  

    public String getUrl() {
        return url;
    }
}

